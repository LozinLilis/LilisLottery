package org.lozin.tool.file;

import org.lozin.lilislottery.main.LilisLottery;
import org.lozin.tool.string.Logger;
import org.lozin.tool.yaml.YamlService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
	static void regFacts(File file) {
	    if (file == null || !file.exists()) {
	        Logger.log("&f[&c×&f] 文件不存在或为空");
	        return;
	    }
	    LilisLottery plugin = LilisLottery.getInstance();
	    Path basePath = plugin.getDataFolder().toPath();
	    if (file.isFile()) {
	        Path relativePath = basePath.relativize(file.toPath());
	        if (YamlService.regFac(relativePath.toString())) {
	            Logger.log("&f[&a√&f] 已注册文件: &9" + relativePath.toString());
	        }
			else {
	            Logger.log("&f[&c×&f] 无法注册文件: &c" + file.getName());
	        }
	    }
		else if (file.isDirectory()) {
	        File[] files = file.listFiles();
	        if (files == null) {
	            Logger.log("&f[&c×&f] 无法读取目录内容: &c" + file.getAbsolutePath());
	            return;
	        }
	        for (File f : files) {
	            regFacts(f);
	        }
	    }
	}
	static void createFile(String file) {
		File f = new File(LilisLottery.getInstance().getDataFolder(), file);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
