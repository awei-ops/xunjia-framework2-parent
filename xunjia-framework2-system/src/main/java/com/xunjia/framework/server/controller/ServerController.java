package com.xunjia.framework.server.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.xunjia.framework.server.bean.Cpu;
import com.xunjia.framework.server.bean.Disk;
import com.xunjia.framework.server.bean.Memery;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

@RestController
@RequestMapping("/server")
public class ServerController {

	private static final int OSHI_WAIT_SECOND = 1000;

	@RequestMapping("/toServerInfo")
	public ModelAndView toServerInfo() {
		ModelAndView mav = new ModelAndView("framework/server/info");
		mav.addObject("sysInfo", this.getSysInfo());
		return mav;
	}
	
	/*
	 * @RequestMapping("/getServerInfo") public Server getServerInfo() throws
	 * Exception { Server server = new Server(); return server; }
	 */
	
	@RequestMapping("/getCpuInfo")
	public Map<String, Object> getCpuInfo() {
		SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Calendar ca = Calendar.getInstance();
        String time = String.valueOf(ca.get(Calendar.MINUTE))+ ":" + String.valueOf(ca.get(Calendar.SECOND));
        resultMap.put("time", time);
        resultMap.put("cpu", setCpuInfo(hal.getProcessor()));
        return resultMap;
	}
	
	@RequestMapping("/getMemeryInfo")
	public Memery getMemeryInfo() {
		SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        
        return setMemInfo(hal.getMemory());
	}
	
	@RequestMapping("/getDiskInfo")
	public List<Disk> getDiskInfo() {
		SystemInfo si = new SystemInfo();
        return setDisksInfo(si.getOperatingSystem());
	}
	
	@RequestMapping("/getSysInfo")
	public com.xunjia.framework.server.bean.System getSysInfo() {
		return this.setSysInfo();
	}
	
	/**
     * 设置CPU信息
     */
	private Cpu setCpuInfo(CentralProcessor processor)
    {
		Cpu cpu = new Cpu();
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        cpu.setCpuNum(processor.getLogicalProcessorCount());
        cpu.setTotal(totalCpu);
        cpu.setSys(cSys);
        cpu.setUsed(user);
        cpu.setWait(iowait);
        cpu.setFree(idle);
        return cpu;
    }
	
	/**
     * 设置内存信息
     */
    private Memery setMemInfo(GlobalMemory memory)
    {
    	Memery mem = new Memery();
        mem.setTotal(memory.getTotal());
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        mem.setFree(memory.getAvailable());
        return mem;
    }
    
    /**
     * 设置服务器信息
     */
    private com.xunjia.framework.server.bean.System setSysInfo()
    {
    	com.xunjia.framework.server.bean.System sys = new com.xunjia.framework.server.bean.System();
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Properties props = java.lang.System.getProperties();
        assert addr != null;
        sys.setComputerName(addr.getHostName());
        sys.setComputerIp(NetUtil.getLocalhostStr());
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
        return sys;
    }

    /**
     * 设置磁盘信息
     */
    private List<Disk> setDisksInfo(OperatingSystem os)
    {
    	List<Disk> disks = new LinkedList<Disk>();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray)
        {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            Disk disk = new Disk();
            disk.setDirName(fs.getMount());
            disk.setSysTypeName(fs.getType());
            disk.setTypeName(fs.getName());
            disk.setTotal(convertFileSize(total));
            disk.setFree(convertFileSize(free));
            disk.setUsed(convertFileSize(used));
            disk.setUsage(total > 0 ? NumberUtil.mul(NumberUtil.div(used, total, 4), 100) : 0);
            disks.add(disk);
        }
        return disks;
    }

    /**
     * 字节转换
     */
    public String convertFileSize(long size)
    {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb)
        {
            return String.format("%.1f GB", (float) size / gb);
        }
        else if (size >= mb)
        {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        }
        else if (size >= kb)
        {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        }
        else
        {
            return String.format("%d B", size);
        }
    }
}
