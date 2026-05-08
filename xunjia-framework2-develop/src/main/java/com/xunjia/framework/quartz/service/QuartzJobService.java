package com.xunjia.framework.quartz.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.xunjia.framework.utils.DateUtils;
import com.xunjia.framework.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xunjia.framework.common.response.ResponseData;
import com.xunjia.framework.common.response.ResponseMsg;
import com.xunjia.framework.quartz.entity.QuartzJob;
import com.xunjia.framework.quartz.repository.IQuartzJobRepository;
import com.xunjia.framework.utils.ReflectorUtils;
import com.xunjia.framework.utils.StringUtils;

@Service
@Transactional
@Slf4j
public class QuartzJobService {

	@Autowired
	private IQuartzJobRepository repo;
	
	@Autowired
    private Scheduler scheduler;
	
	public ResponseData<Boolean> save(QuartzJob job){
		ResponseData<Boolean> resp;
		job.setCreateTime(new Date());
		try {
			if (ReflectorUtils.isClassPresent(job.getJobClassName())) {
				repo.save(job);
				resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
			} else {
				resp = ResponseData.getFail(ResponseMsg.CLASS_NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("QuartzJobService.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> update(QuartzJob job){
		ResponseData<Boolean> resp;
		try {
			QuartzJob existJob = repo.findById(job.getId()).get();
			Date createDate = existJob.getCreateTime();
			job.setCreateTime(createDate);

			String jobStatus = this.getJobStatus(existJob.getJobName() + existJob.getTriggerSalt(), existJob.getJobGroup());

			if (("运行中".equals(jobStatus) || "阻塞".equals(jobStatus)) && !job.getCronExpression().equals(existJob.getCronExpression())) {
				String triggerSalt = String.valueOf(System.currentTimeMillis());

				//精确任务
	            TriggerKey triggerKey = TriggerKey.triggerKey(existJob.getJobName() + triggerSalt, job.getJobGroup());
	            //获取这个任务的触发器
	            CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
	            //设置新的定时器
	            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression())
						.withMisfireHandlingInstructionDoNothing();
	            //给触发器绑定新的定时任务
	            trigger = trigger.getTriggerBuilder()
	                    .withSchedule(cronScheduleBuilder)
	                    .withIdentity(triggerKey)
	                    .build();
	            //绑定定时器和触发器
	            scheduler.rescheduleJob(triggerKey, trigger);

	            job.setTriggerSalt(triggerSalt);
			} else {
				job.setTriggerSalt(existJob.getTriggerSalt());
			}

			repo.save(job);
			
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			log.error("QuartzJobService.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> delete(String[] ids){
		ResponseData<Boolean> resp;
		try {
			for (String id : ids) {
				QuartzJob job = repo.findById(id).get();
				//定位任务
	            JobKey jobKey = JobKey.jobKey(job.getJobName() + job.getTriggerSalt(), job.getJobGroup());
	            scheduler.deleteJob(jobKey);
			}
			repo.deleteByIds(ids);
            resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			log.error("QuartzJobService.delete方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public QuartzJob findById(String id) {
		return repo.findById(id).get();
	}
	
	public synchronized ResponseData<Boolean> start(String id){
		ResponseData<Boolean> resp;
        Date startDate = null;
        TriggerKey triggerKey = null;
		CronTrigger cronTrigger = null;
        String triggerSalt = String.valueOf(System.currentTimeMillis());
		try {
			QuartzJob job = repo.findById(id).get();

			String jobStatus = null;
			if (job.getTriggerSalt() == null){
				jobStatus = "未执行";
			} else {
				jobStatus = this.getJobStatus(job.getJobName() + job.getTriggerSalt(), job.getJobGroup());
			}

			if ("未执行".equals(jobStatus) || "完成".equals(jobStatus)) {
				// 创建任务信息
				triggerKey = TriggerKey.triggerKey(job.getJobName() + triggerSalt, job.getJobGroup());

				// 创建一个触发器
				cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
				// 创建一个定时任务
				Class<? extends Job> jobObject = (Class<? extends Job>) Class.forName(job.getJobClassName());
				JobDetail jobDetail = JobBuilder.newJob(jobObject)
						.withIdentity(job.getJobName() + triggerSalt, job.getJobGroup()).build();
				// 将任务对对象产地给任务工厂 生产任务
				//jobDetail.getJobDataMap().put("job", job);
				if (cronTrigger == null) {
					// 创建一个定时器
					CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression())
							.withMisfireHandlingInstructionDoNothing();
					// 创建新的触发器
					cronTrigger = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
							.withIdentity(job.getJobName() + triggerSalt, job.getJobGroup()).build();
				}
				// 绑定触发器和任务详情
				startDate = scheduler.scheduleJob(jobDetail, cronTrigger);
			} else if ("暂停".equals(jobStatus)) {
				triggerSalt = job.getTriggerSalt();
				triggerKey = TriggerKey.triggerKey(job.getJobName() + triggerSalt, job.getJobGroup());
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression())
						.withMisfireHandlingInstructionDoNothing();
                // 创建新的触发器
                cronTrigger = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
                        .withIdentity(job.getJobName() + triggerSalt, job.getJobGroup()).build();
				startDate = scheduler.rescheduleJob(triggerKey, cronTrigger);
			} else {
				throw new Exception("任务状态异常，无法启动。");
			}

            //记录日志
			String logInfo = "任务名称：" + job.getJobName() + " 任务组:" + job.getJobGroup();
			logInfo += " TriggerKey: " + triggerKey.toString();

			if (startDate != null){
				job.setTriggerSalt(triggerSalt);
				job.setStarted(1);
				resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
				log.info("任务启动成功：" + DateUtils.format(startDate, "yyyy-MM-dd HH:mm:ss"));
				log.info(logInfo);
			} else {
				resp = ResponseData.getFail(ResponseMsg.COMMON_FAIL);
				log.info("任务启动失败：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				log.info(logInfo);
			}
			repo.save(job);
		} catch (Exception e) {
			log.error("任务启动失败，任务id是" + id);
			log.error("QuartzJobService.start方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public ResponseData<Boolean> pause(String id){
		ResponseData<Boolean> resp;
		try {
			QuartzJob job = repo.findById(id).get();
			JobKey jobKey = JobKey.jobKey(job.getJobName() + job.getTriggerSalt(), job.getJobGroup());
			scheduler.pauseJob(jobKey);

			job.setStarted(0);
            repo.save(job);
			resp = ResponseData.getSuccess(ResponseMsg.COMMON_SUCCESS);
		} catch (Exception e) {
			log.error("QuartzJobService.pause方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	public Page<QuartzJob> findQuartzJobs(String name, int page, int rows){
		Specification<QuartzJob> spec = new Specification<QuartzJob>() {
			public Predicate toPredicate(Root<QuartzJob> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = new LinkedList<Predicate>();
				if (!StringUtils.isEmpty(name)) {
					Predicate predicate = cb.like(root.get("jobName").as(String.class), "%" + name + "%");
					predicates.add(predicate);
				}
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Sort sort = Sort.by(Direction.DESC, "createTime");
		Pageable pageable = PageRequest.of(page - 1, rows, sort);
		Page<QuartzJob> pageData = null;
		try {
			pageData = repo.findAll(spec, pageable);
			List<QuartzJob> jobs = pageData.getContent();
			if (!ListUtils.isListEmpty(jobs)){
				for (QuartzJob job : jobs){
					if (job.getTriggerSalt() == null){
						job.setRuntimeState("未执行");
					} else {
						String jobStatus = this.getJobStatus(job.getJobName() + job.getTriggerSalt(), job.getJobGroup());
						job.setRuntimeState(jobStatus);
					}
				}
			}
		} catch (Exception e) {
			log.error("QuartzJobService.findQuartzJobs方法异常。", e);
		}
		return pageData;
	}

	private String getJobStatus(String jobName, String jobGroup) throws SchedulerException {
		Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey(jobName, jobGroup));
		String result = null;
		if (state == Trigger.TriggerState.NONE){
			result = "未执行";
		} else if (state == Trigger.TriggerState.NORMAL){
			result = "运行中";
		} else if (state == Trigger.TriggerState.BLOCKED){
			result = "阻塞";
		} else if (state == Trigger.TriggerState.COMPLETE){
			result = "完成";
		} else if (state == Trigger.TriggerState.PAUSED){
			result = "暂停";
		} else if (state == Trigger.TriggerState.ERROR){
			result = "错误";
		}
		return result;
	}

	public void autoStartJobs() throws SchedulerException {
		List<QuartzJob> startedJobs = repo.findByStarted(1);
		if (!ListUtils.isListEmpty(startedJobs)){
			for (QuartzJob job : startedJobs){
				TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName() + job.getTriggerSalt(), job.getJobGroup());
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression())
						.withMisfireHandlingInstructionDoNothing();
				// 创建新的触发器
				CronTrigger cronTrigger = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder)
						.withIdentity(job.getJobName() + job.getTriggerSalt(), job.getJobGroup()).build();
				Date startDate = scheduler.rescheduleJob(triggerKey, cronTrigger);

				//记录日志
				String logInfo = "任务名称：" + job.getJobName() + " 任务组:" + job.getJobGroup();
				logInfo += " TriggerKey: " + triggerKey.toString();

				if (startDate != null) {
					log.info("任务启动成功：" + DateUtils.format(startDate, "yyyy-MM-dd HH:mm:ss"));
					log.info(logInfo);
				} else {
					log.info("任务启动失败：" + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					log.info(logInfo);
					job.setStarted(0);
					repo.save(job);
				}
			}
		}
	}
}
