package net.minecraftmurder.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import net.minecraftmurder.inventory.MItem;
import net.minecraftmurder.signs.MSign;
import net.minecraftmurder.signs.MSignBuy;
import net.minecraftmurder.signs.MSignMatch;
import net.minecraftmurder.tools.MLogger;
import net.minecraftmurder.tools.SimpleFile;

public class SignManager {
	public static final String PATH_SIGN = "plugins/Murder/signs.yml";
	
	private static List<MSign> mSigns;
	
	public static void initialize() {
		mSigns = new ArrayList<MSign>();
		load();
	}
	
	static void load () {
		YamlConfiguration config = SimpleFile.loadConfig(PATH_SIGN, true);
		List<?> list = config.getList("signs", null);
		if (list == null || list.size() < 1) {
			MLogger.log(Level.WARNING, "No signs could be loaded!");
			return;
		}
		for (Object o: list) {
			MSign sign = stringToSign(o.toString());
			if (sign == null) {
				MLogger.log(Level.SEVERE, "Invalid sign.");
				continue;
			} 
			mSigns.add(sign);
		}
	}
	static void save () {
		YamlConfiguration config = SimpleFile.loadConfig(PATH_SIGN, true);
		List<String> savedSigns = new ArrayList<String>();
		for (MSign mSign: mSigns) {
			savedSigns.add(mSign.toString());
		}
		config.set("signs", savedSigns);
		SimpleFile.saveConfig(config, PATH_SIGN);
	}
	
	public static boolean existsSigns (Location location) {
		for (MSign sign: mSigns) {
			if (location.getWorld() != sign.getLocation().getWorld())
				continue;
			if (sign.getLocation().distance(location) <= .5) {
				return true;
			}
		}
		return false;
	}
	public static MSign getMSign (Location location) {
		for (MSign sign: mSigns) {
			if (sign.getLocation().distance(location) <= .5) {
				return sign;
			}
		}
		return null;
	}
	public static void addMSign (MSign sign) {
		mSigns.add(sign);
		save();
	}
	public static void removeSign (Location location) {
		for (MSign sign: mSigns) {
			if (sign.getLocation().equals(location)) {
				mSigns.remove(sign);
				MLogger.log(Level.INFO, "Sign removed.");
			} else {
				MLogger.log(Level.WARNING, "Can't remove sign entry that doesn't exist: " + location.toString());
			}
		}
		save();
	}
	
	public static void updateSigns () {
		for (MSign mSign: mSigns) {
			mSign.update();
		}
	}
	
	private static MSign stringToSign (String sign) {
		String[] split = sign.split(" ");
		
		if (split.length == 6 && split[0].equalsIgnoreCase("match")) {
			World world = Bukkit.getWorld(split[1]);
			if (world == null)
				return null;
			double x = Double.parseDouble(split[2]);
			double y = Double.parseDouble(split[3]);
			double z = Double.parseDouble(split[4]);
			int index = Integer.parseInt(split[5]);
			return new MSignMatch(new Location(world, x, y, z), index); 
		} else if (split.length == 6 && split[0].equalsIgnoreCase("shop")) {
			World world = Bukkit.getWorld(split[1]);
			if (world == null)
				return null;
			double x = Double.parseDouble(split[2]);
			double y = Double.parseDouble(split[3]);
			double z = Double.parseDouble(split[4]);
			MItem mItem = MItem.getItem(split[5]);
			return new MSignBuy(new Location(world, x, y, z), mItem);
		}
		return null;
	}
}
 