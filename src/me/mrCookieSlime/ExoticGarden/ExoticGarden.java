package me.mrCookieSlime.ExoticGarden;

import java.io.File;
import java.util.*;

import me.mrCookieSlime.Slimefun.api.Slimefun;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomPotion;
import me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Juice;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class ExoticGarden extends JavaPlugin {

	static List<Berry> berries = new ArrayList<Berry>();
	static List<Tree> trees = new ArrayList<Tree>();
	static Map<String, ItemStack> items = new HashMap<String, ItemStack>();
	Category category_main, category_food, category_drinks, category_magic;
	Config cfg;
	public static CoreProtectAPI coreProtectAPI;
	private static boolean skullitems;

	public static ItemStack KITCHEN = new CustomItem(Material.CAULDRON, "&eKitchen", new String[] {"", "&a&oYou can make a bunch of different yummies here!", "&a&oThe result goes in the Furnace output slot"});

	@Override
	public void onEnable() {
		if (!new File("plugins/ExoticGarden").exists()) new File("plugins/ExoticGarden").mkdirs();
    	if (!new File("plugins/ExoticGarden/schematics").exists()) new File("plugins/ExoticGarden/schematics").mkdirs();
		if(Bukkit.getPluginManager().getPlugin("CoreProtect")!=null)
			coreProtectAPI = ((CoreProtect)Bukkit.getPluginManager().getPlugin("CoreProtect")).getAPI();
    	PluginUtils utils = new PluginUtils(this);
		utils.setupConfig();
		cfg = utils.getConfig();
		utils.setupMetrics();
		utils.setupUpdater(88425, getFile());

		skullitems = cfg.getBoolean("options.item-heads");

		category_main = new Category(new CustomItem(getSkull(Material.NETHER_WART, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="), "&aExotic Garden - Plants and Fruits", "", "&a> Click to open"));
		category_food = new Category(new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&aExotic Garden - Food", "", "&a> Click to open"));
		category_drinks = new Category(new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&aExotic Garden - Drinks", "", "&a> Click to open"));
		category_magic = new Category(new CustomItem(Material.BLAZE_POWDER, "&5Exotic Garden - Magical Plants", "", "&a> Click to open"));

		new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.ICE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ=="), "&bIce Cube"), "ICE_CUBE", RecipeType.GRIND_STONE,
		new ItemStack[] {new ItemStack(Material.ICE), null, null, null, null, null, null, null, null}, new CustomItem(new CustomItem(getSkull(Material.ICE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ=="), "&bIce Cube"), 4))
		.register();

		Kitchen.registerKitchen(this);

		registerBerry("葡萄", "&c", Color.RED, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlOTc2NDliZDk5OTk1NTQxM2ZjYmYwYjI2OWM5MWJlNDM0MmIxMGQwNzU1YmFkN2ExN2U5NWZjZWZkYWIwIn19fQ=="));
		registerBerry("蓝莓", "&9", Color.BLUE, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="));
		registerBerry("接骨木果", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU0ODgzYTFlMjJjMzI0ZTc1MzE1MWUyYWM0MjRjNzRmMWNjNjQ2ZWVjOGVhMGRiMzQyMGYxZGQxZDhiIn19fQ=="));
		registerBerry("树莓", "&d", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODI2MmM0NDViYzJkZDFjNWJiYzhiOTNmMjQ4MmY5ZmRiZWY0OGE3MjQ1ZTFiZGIzNjFkNGE1NjgxOTBkOWI1In19fQ=="));
		registerBerry("黑莓", "&8", Color.GRAY, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OWY4Yjc4YzQyZTI3MmE2NjlkNmU2ZDE5YmE4NjUxYjcxMGFiNzZmNmI0NmQ5MDlkNmEzZDQ4Mjc1NCJ9fX0="));
		registerBerry("蔓越莓", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVmZTZjNzE4ZmJhNzE5ZmY2MjIyMzdlZDllYTY4MjdkMDkzZWZmYWI4MTRiZTIxOTJlOTY0M2UzZTNkNyJ9fX0="));
		registerBerry("越桔", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA0ZTU0YmYyNTVhYjBiMWM0OThjYTNhMGNlYWU1YzdjNDVmMTg2MjNhNWEwMmY3OGE3OTEyNzAxYTMyNDkifX19"));
		registerBerry("草莓", "&4", Color.FUCHSIA, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0="));

		registerPlant("番茄", "&4", Material.APPLE, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkxNzIyMjZkMjc2MDcwZGMyMWI3NWJhMjVjYzJhYTU2NDlkYTVjYWM3NDViYTk3NzY5NWI1OWFlYmQifX19"));
		registerPlant("生菜", "&2", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3ZGQ4NDJjOTc1ZDhmYjAzYjFhZGQ2NmRiODM3N2ExOGJhOTg3MDUyMTYxZjIyNTkxZTZhNGVkZTdmNSJ9fX0="));
		registerPlant("茶叶", "&a", Material.OAK_LEAVES, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUxNGM4YjQ2MTI0N2FiMTdmZTM2MDZlNmUyZjRkMzYzZGNjYWU5ZWQ1YmVkZDAxMmI0OThkN2FlOGViMyJ9fX0="));
		registerPlant("卷心菜", "&2", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNkNmQ2NzMyMGM5MTMxYmU4NWExNjRjZDdjNWZjZjI4OGYyOGMyODE2NTQ3ZGIzMGEzMTg3NDE2YmRjNDViIn19fQ=="));
		registerPlant("地瓜", "&6", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZmNDg1NzhiNjY4NGUxNzk5NDRhYjFiYzc1ZmVjNzVmOGZkNTkyZGZiNDU2ZjZkZWY3NjU3NzEwMWE2NiJ9fX0="));
		registerPlant("荠菜籽", "&e", Material.GOLD_NUGGET, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ1M2E0MjQ5NWZhMjdmYjkyNTY5OWJjM2U1ZjI5NTNjYzJkYzMxZDAyN2QxNGZjZjdiOGMyNGI0NjcxMjFmIn19fQ=="));

		registerPlant("玉米", "&6", Material.GOLDEN_CARROT, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJkMzgwMmU1ZmFjMDNhZmFiNzQyYjBmM2NjYTQxYmNkNDcyM2JlZTkxMWQyM2JlMjljZmZkNWI5NjVmMSJ9fX0="));
		registerPlant("菠萝", "&6", Material.GOLDEN_CARROT, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlZGRkODJlNTc1ZGZkNWI3NTc5ZDg5ZGNkMjM1MGM5OTFmMDQ4M2E3NjQ3Y2ZmZDNkMmM1ODdmMjEifX19"));

		registerTree("苹果", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "OAK_APPLE", "&c", Color.FUCHSIA, "苹果汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("椰子", Material.COCOA_BEANS, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQyN2RlZDU3Yjk0Y2Y3MTViMDQ4ZWY1MTdhYjNmODViZWY1YTdiZTY5ZjE0YjE1NzNlMTRlN2U0MmUyZTgifX19", "COCONUT", "&6", Color.MAROON, "椰子汁", false, Material.SAND);
		registerTree("樱桃", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyMDc2NmI4N2QyNDYzYzM0MTczZmZjZDU3OGIwZTY3ZDE2M2QzN2EyZDdjMmU3NzkxNWNkOTExNDRkNDBkMSJ9fX0=", "CHERRY", "&c", Color.FUCHSIA, "樱桃汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("石榴", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "POMEGRANATE", "&4", Color.RED, "石榴汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("柠檬", Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU3ZmQ1NmNhMTU5Nzg3NzkzMjRkZjUxOTM1NGI2NjM5YThkOWJjMTE5MmM3YzNkZTkyNWEzMjliYWVmNmMifX19", "LEMON", "&e", Color.YELLOW, "柠檬汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("李子", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlkNjY0MzE5ZmYzODFiNGVlNjlhNjk3NzE1Yjc2NDJiMzJkNTRkNzI2Yzg3ZjY0NDBiZjAxN2E0YmNkNyJ9fX0=", "PLUM", "&5", Color.RED, "李子汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("酸橙", Material.SLIME_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE1MTUzNDc5ZDlmMTQ2YTVlZTNjOWUyMThmNWU3ZTg0YzRmYTM3NWU0Zjg2ZDMxNzcyYmE3MWY2NDY4In19fQ==", "LIME", "&a", Color.LIME, "酸橙汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("桔子", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViMWRiNTQ3ZDFiNzk1NmQ0NTExYWNjYjE1MzNlMjE3NTZkN2NiYzM4ZWI2NDM1NWEyNjI2NDEyMjEyIn19fQ==", "ORANGE", "&6", Color.ORANGE, "橘子汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("桃子", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNiYTQxZmU4Mjc1Nzg3MWU4Y2JlYzlkZWQ5YWNiZmQxOTkzMGQ5MzM0MWNmODEzOWQxZGZiZmFhM2VjMmE1In19fQ==", "PEACH", "&5", Color.RED, "桃子汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("梨", Material.SLIME_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRlMjhkZjg0NDk2MWE4ZWNhOGVmYjc5ZWJiNGFlMTBiODM0YzY0YTY2ODE1ZThiNjQ1YWVmZjc1ODg5NjY0YiJ9fX0=", "PEAR", "&a", Color.LIME, "梨汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("火龙果", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3ZDczYTkxYjUyMzkzZjJjMjdlNDUzZmI4OWFiM2Q3ODQwNTRkNDE0ZTM5MGQ1OGFiZDIyNTEyZWRkMmIifX19\\", "DRAGON_FRUIT", "&d", Color.FUCHSIA, "火龙果汁", true, Material.DIRT, Material.GRASS_BLOCK);

		registerDishes();

		registerMagicalPlant("煤炭", new ItemStack(Material.COAL, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc4OGY1ZGRhZjUyYzU4NDIyODdiOTQyN2E3NGRhYzhmMDkxOWViMmZkYjFiNTEzNjVhYjI1ZWIzOTJjNDcifX19",
		new ItemStack[] {null, new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), new ItemStack(Material.WHEAT_SEEDS), new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), null});

		registerMagicalPlant("铁", new ItemStack(Material.IRON_INGOT), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI5N2JkZjkyYjYxOTI2ZTM5ZjVjZGRmMTJmOGY3MTMyOTI5ZGVlNTQxNzcxZTBiNTkyYzhiODJjOWFkNTJkIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), getItem("COAL_PLANT"), new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), null});

		registerMagicalPlant("金", SlimefunItems.GOLD_4K, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkZjg5MjI5M2E5MjM2ZjczZjQ4ZjllZmU5NzlmZTA3ZGJkOTFmN2I1ZDIzOWU0YWNmZDM5NGY2ZWNhIn19fQ==",
		new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, getItem("IRON_PLANT"), SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, null});
		
		registerMagicalPlant("铜", new CustomItem(SlimefunItems.COPPER_DUST, 8),  "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkyM2RiZmQ4ZjMxMTI2OTBiYjVhNjE2OGE4ZDNjYTVhYjllN2Q0M2IxZDExY2ZjYjY0M2RlN2RmZTIxIn19fQ==",
		new ItemStack[] {null, SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, getItem("GOLD_PLANT"), SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, null});

		registerMagicalPlant("红石", new ItemStack(Material.REDSTONE, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThkZWVlNTg2NmFiMTk5ZWRhMWJkZDc3MDdiZGI5ZWRkNjkzNDQ0ZjFlM2JkMzM2YmQyYzc2NzE1MWNmMiJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), getItem("GOLD_PLANT"), new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), null});

		registerMagicalPlant("青金石", new ItemStack(Material.LAPIS_LAZULI, 16), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFhMGQwZmVhMWFmYWVlMzM0Y2FiNGQyOWQ4Njk2NTJmNTU2M2M2MzUyNTNjMGNiZWQ3OTdlZDNjZjU3ZGUwIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), getItem("REDSTONE_PLANT"), new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), null});

		registerMagicalPlant("末影珍珠", new ItemStack(Material.ENDER_PEARL, 4), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUzNWFhZGU4MTI5MmU2ZmY0Y2QzM2RjMGVhNmExMzI2ZDA0NTk3YzBlNTI5ZGVmNDE4MmIxZDE1NDhjZmUxIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), getItem("LAPIS_PLANT"), new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), null});

		registerMagicalPlant("石英", new ItemStack(Material.QUARTZ, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZkZTU4ZDU4M2MxMDNjMWNkMzQ4MjQzODBjOGE0NzdlODk4ZmRlMmViOWE3NGU3MWYxYTk4NTA1M2I5NiJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), getItem("ENDER_PLANT"), new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), null});

		registerMagicalPlant("钻石", new ItemStack(Material.DIAMOND), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg4Y2Q2ZGQ1MDM1OWM3ZDU4OThjN2M3ZTNlMjYwYmZjZDNkY2IxNDkzYTg5YjllODhlOWNiZWNiZmU0NTk0OSJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), getItem("QUARTZ_PLANT"), new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), null});

		registerMagicalPlant("绿宝石", new ItemStack(Material.EMERALD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZjNDk1ZDFlNmViNTRhMzg2MDY4YzZjYjEyMWM1ODc1ZTAzMWI3ZjYxZDcyMzZkNWYyNGI3N2RiN2RhN2YifX19",
		new ItemStack[] {null, new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), getItem("DIAMOND_PLANT"), new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), null});

		registerMagicalPlant("萤石", new ItemStack(Material.GLOWSTONE_DUST, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2JlZDhkZjcxNGNlYTA2M2U0NTdiYTVlODc5MzExNDFkZTI5M2RkMWQ5YjkxNDZiMGY1YWIzODM4NjYifX19",
		new ItemStack[] {null, new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), getItem("REDSTONE_PLANT"), new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), null});

		registerMagicalPlant("黑曜石", new ItemStack(Material.OBSIDIAN, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg0MGI4N2Q1MjI3MWQyYTc1NWRlZGM4Mjg3N2UwZWQzZGY2N2RjYzQyZWE0NzllYzE0NjE3NmIwMjc3OWE1In19fQ==",
		new ItemStack[] {null, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), getItem("LAPIS_PLANT"), new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), null});

		registerMagicalPlant("史莱姆", new ItemStack(Material.SLIME_BALL, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBlNjVlNmU1MTEzYTUxODdkYWQ0NmRmYWQzZDNiZjg1ZThlZjgwN2Y4MmFhYzIyOGE1OWM0YTk1ZDZmNmEifX19",
		new ItemStack[] {null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), getItem("ENDER_PLANT"), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), null});

		final ItemStack grass_seeds = new CustomItem(Material.PUMPKIN_SEEDS, "&r草种", "", "&7&o可以种在泥土上");

		final SlimefunItem crook = new SlimefunItem(Categories.TOOLS, new CustomItem(Material.WOODEN_HOE, "&r钩子", "", "&7+ &b25% &7树苗掉落率"), "CROOK", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null});
		crook.register(false, new BlockBreakHandler() {
			@Override
			public boolean onBlockBreak(BlockBreakEvent e, ItemStack i, int fortune, List<ItemStack> drops) {
				if (SlimefunManager.isItemSimiliar(i, crook.getItem(), true)) {
					PlayerInventory.damageItemInHand(e.getPlayer());
					if (Tag.LEAVES.isTagged(e.getBlock().getType()) && CSCoreLib.randomizer().nextInt(100) < 25) {
						ItemStack sapling = new ItemStack(e.getBlock().getType());
						drops.add(sapling);
					}
					return true;
				}
				return false;
			}
		});

		new SlimefunItem(category_main, grass_seeds, "GRASS_SEEDS", new RecipeType(new CustomItem(Material.GRASS, "&7打破草")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(false, new ItemInteractionHandler() {
			@Override
			public boolean onRightClick(ItemUseEvent e, Player p, ItemStack i) {
				if (SlimefunManager.isItemSimiliar(i, grass_seeds, true)) {
					Block b = e.getClickedBlock();
					if (b != null && b.getType() == Material.DIRT) {
						PlayerInventory.consumeItemInHand(p);
						b.setType(Material.GRASS_BLOCK);
						logPlacement(p.getName(),b.getLocation(),b.getType(),b.getBlockData());
						if (b.getRelative(BlockFace.UP).getType() == Material.AIR || b.getRelative(BlockFace.UP).getType() == Material.CAVE_AIR) {
							b.getRelative(BlockFace.UP).setType(Material.GRASS);
							logPlacement(p.getName(),b.getLocation(),b.getType(),b.getBlockData());
						}
						b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.GRASS);
					}
					return true;
				}
				else return false;
			}
		});

		new PlantsListener(this);
		new FoodListener(this);

		items.put("WHEAT_SEEDS", new ItemStack(Material.WHEAT_SEEDS));
		items.put("PUMPKIN_SEEDS", new ItemStack(Material.PUMPKIN_SEEDS));
		items.put("MELON_SEEDS", new ItemStack(Material.MELON_SEEDS));
		items.put("OAK_SAPLING", new ItemStack(Material.OAK_SAPLING));
		items.put("SPRUCE_SAPLING", new ItemStack(Material.SPRUCE_SAPLING));
		items.put("BIRCH_SAPLING", new ItemStack(Material.BIRCH_SAPLING));
		items.put("JUNGLE_SAPLING", new ItemStack(Material.JUNGLE_SAPLING));
		items.put("ACACIA_SAPLING", new ItemStack(Material.ACACIA_SAPLING));
		items.put("DARK_OAK_SAPLING", new ItemStack(Material.DARK_OAK_SAPLING));
		items.put("GRASS_SEEDS", grass_seeds);

		Iterator<String> iterator = items.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			cfg.setDefaultValue("grass-drops." + key, true);
			if (!cfg.getBoolean("grass-drops." + key)) iterator.remove();
		}
		cfg.save();
	}

	private void registerDishes() {
		new Juice(category_drinks, new CustomPotion("&a酸橙冰沙", Color.LIME, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), "LIME_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LIME_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&4番茄汁", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o恢复 &b&o" + "3.0" + " &7&o饱食度"), "TOMATO_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem("TOMATO"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&c葡萄酒", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), "WINE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("GRAPE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&e柠檬冰茶", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "LEMON_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&d树莓冰茶", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "RASPBERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("RASPBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&d桃子冰茶", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "PEACH_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PEACH"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&4草莓冰茶", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "STRAWBERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("STRAWBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&c樱桃冰茶", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "CHERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("CHERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&6泰国茶", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 14, 0), "", "&7&o恢复 &b&o" + "7.0" + " &7&o饱食度"), "THAI_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), SlimefunItems.HEAVY_CREAM, getItem("COCONUT_MILK"), null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM0ODdkNDU3ZjkwNjJkNzg3YTNlNmNlMWM0NjY0YmY3NDAyZWM2N2RkMTExMjU2ZjE5YjM4Y2U0ZjY3MCJ9fX0="), "&r南瓜面包", "", "&7&o恢复 &b&o" + "4.0" + " &7&o饱食度"), "PUMPKIN_BREAD",
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		8)
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.MILK_BUCKET, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4ZDUzNmM4YzJjMjU5NmJjYzE3MDk1OTBhOWQ3ZTMzMDYxYzU2ZTY1ODk3NGNkODFiYjgzMmVhNGQ4ODQyIn19fQ=="), "&rMayo"), "MAYO", RecipeType.GRIND_STONE, false,
		new ItemStack[] {new ItemStack(Material.EGG), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI5ZTk5NjIxYjk3NzNiMjllMzc1ZTYyYzY0OTVmZjFhYzg0N2Y4NWIyOTgxNmMyZWI3N2I1ODc4NzRiYTYyIn19fQ=="), "&ej芥末"), "MUSTARD", RecipeType.GRIND_STONE, false,
		new ItemStack[] {getItem("MUSTARD_SEED"), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.MILK_BUCKET, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg2ZjE5YmYyM2QyNDhlNjYyYzljOGI3ZmExNWVmYjhhMWYxZDViZGFjZDNiODYyNWE5YjU5ZTkzYWM4YSJ9fX0="), "&c烤肉酱"), "BBQ_SAUCE", RecipeType.ENHANCED_CRAFTING_TABLE, false,
		new ItemStack[] {getItem("TOMATO"), getItem("MUSTARD"), getItem("SALT"), new ItemStack(Material.SUGAR), null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new CustomItem(Material.SUGAR, "&r棒子面"), "CORNMEAL", RecipeType.GRIND_STONE,
		new ItemStack[] {getItem("CORN"), null, null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.COCOA_BEANS, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE5Zjk0OGQxNzcxOGFkYWNlNWRkNmUwNTBjNTg2MjI5NjUzZmVmNjQ1ZDcxMTNhYjk0ZDE3YjYzOWNjNDY2In19fQ=="), "&r巧克力棒", "", "&7&o恢复 &b&o" + "1.5" + " &7&o饱食度"), "CHOCOLATE_BAR",
		new ItemStack[] {new ItemStack(Material.COCOA_BEANS), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&r土豆沙拉", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "POTATO_SALAD",
		new ItemStack[] {new ItemStack(Material.BAKED_POTATO), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r鸡肉三明治", "", "&7&o恢复 &b&o" + "5.5" + " &7&o饱食度"), "CHICKEN_SANDWICH",
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r鱼肉三明治", "", "&7&o恢复 &b&o" + "5.5" + " &7&o饱食度"), "FISH_SANDWICH",
		new ItemStack[] {new ItemStack(Material.COOKED_COD), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&鸡蛋沙拉", new String[] {"", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"}), "EGG_SALAD",
		new ItemStack[] {new ItemStack(Material.EGG), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&4f番茄汤", new String[] {"", "&7&o恢复 &b&o" + "5.5" + " &7&o饱食度"}), "TOMATO_SOUP",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("TOMATO"), null, null, null, null, null, null, null},
		5)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&c草莓沙拉", new String[] {"", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"}), "STRAWBERRY_SALAD",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&c葡萄沙拉", new String[] {"", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"}), "GRAPE_SALAD",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("GRAPE"), null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&r芝士蛋糕", "", "&7&o恢复 &b&o" + "8.0" + " &7&o饱食度"), "CHEESECAKE",
		new ItemStack[] {new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&c樱桃芝士蛋糕", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "CHERRY_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), getItem("CHERRY"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&9蓝莓芝士蛋糕", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "BLUEBERRY_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&6南瓜芝士蛋糕", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "PUMPKIN_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.PUMPKIN), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&6加糖的梨芝士蛋糕", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "SWEETENED_PEAR_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.SUGAR), getItem("PEAR"), null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(Material.COOKIE, "&6饼干", "", "&7&o恢复 &b&o" + "2.0" + " &7&o饱食度"), "BISCUIT",
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, null, null, null, null, null, null, null}, 
		2)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZjMzY1MjNjMmQxMWI4YzhlYTJlOTkyMjkxYzUyYTY1NDc2MGVjNzJkY2MzMmRhMmNiNjM2MTY0ODFlZSJ9fX0="), "&8黑莓馅饼", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "BLACKBERRY_COBBLER",
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("BLACKBERRY"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&r帕夫洛娃", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "PAVLOVA",
		new ItemStack[] {getItem("LEMON"), getItem("STRAWBERRY"), new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(Material.GOLDEN_CARROT, "&6玉米棒", "", "&7&o恢复 &b&o" + "4.5" + " &7&o饱食度"), "CORN_ON_THE_COB",
		new ItemStack[] {SlimefunItems.BUTTER, getItem("CORN"), null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_STEW, "&r奶油玉米", "", "&7&o恢复 &b&o" + "4.0" + " &7&o饱食度"), "CREAMED_CORN",
		new ItemStack[] {SlimefunItems.HEAVY_CREAM, getItem("CORN"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		2)
		.register();
		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKED_PORKCHOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdiYTIyZDVkZjIxZTgyMWE2ZGU0YjhjOWQzNzNhM2FhMTg3ZDhhZTc0ZjI4OGE4MmQyYjYxZjI3MmU1In19fQ=="), "&r培根", "", "&7&o恢复 &b&o" + "1.5" + " &7&o饱食度"), "BACON",
		new ItemStack[] {new ItemStack(Material.COOKED_PORKCHOP), null, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r三明治", "", "&7&o恢复 &b&o" + "9.5" + " &7&o饱食度"), "SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rBLT", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "BLT",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r绿叶鸡肉三明治", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "LEAFY_CHICKEN_SANDWICH",
		new ItemStack[] {getItem("CHICKEN_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		1)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r绿叶鱼肉三明治", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "LEAFY_FISH_SANDWICH",
		new ItemStack[] {getItem("FISH_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r汉堡包", "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), "HAMBURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_BEEF), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r乳酪汉堡", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "CHEESEBURGER",
		new ItemStack[] {getItem("HAMBURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r培根芝士汉堡", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "BACON_CHEESEBURGER",
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("BACON"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r豪华芝士汉堡", "", "&7&o恢复 &b&o" + "8.0" + " &7&o饱食度"), "DELUXE_CHEESEBURGER",
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19"), "&r胡萝卜蛋糕", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "CARROT_CAKE",
		new ItemStack[] {new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r鸡肉汉堡", "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), "CHICKEN_BURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_CHICKEN), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r鸡肉芝士汉堡", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "CHICKEN_CHEESEBURGER",
		new ItemStack[] {getItem("CHICKEN_BURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r培根汉堡", "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), "BACON_BURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r培根三明治", "", "&7&o恢复 &b&o" + "9.5" + " &7&o饱食度"), "BACON_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), getItem("MAYO"), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0="), "&r玉米饼", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "TACO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0="), "&r鱼肉玉米饼", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "FISH_TACO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_COD), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(Material.COOKIE, "&cJammy Dodger", "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), "JAMMY_DODGER",
		new ItemStack[] {null, getItem("BISCUIT"), null, null, getItem("RASPBERRY_JUICE"), null, null, getItem("BISCUIT"), null}, 
		8)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&r月饼", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "PANCAKES",
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.SUGAR), getItem("BUTTER"), new ItemStack(Material.EGG), new ItemStack(Material.EGG), null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&r蓝莓月饼", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "BLUEBERRY_PANCAKES",
		new ItemStack[] {getItem("PANCAKES"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTYzYjhhZWFmMWRmMTE0ODhlZmM5YmQzMDNjMjMzYTg3Y2NiYTNiMzNmN2ZiYTljMmZlY2FlZTk1NjdmMDUzIn19fQ=="), "&r薯条", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "FRIES",
		new ItemStack[] {new ItemStack(Material.POTATO), getItem("SALT"), null, null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&r爆米花", "", "&7&o恢复 &b&o" + "4.0" + " &7&o饱食度"), "POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&r爆米花 &7(甜)", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "SWEET_POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), new ItemStack(Material.SUGAR), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&r爆米花 &7(咸)", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "SALTY_POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), getItem("SALT"), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&r牧羊人的馅饼", "", "&7&o恢复 &b&o" + "8.0" + " &7&o饱食度"), "SHEPARDS_PIE",
		new ItemStack[] {getItem("CABBAGE"), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&r鸡锅饼", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "CHICKEN_POT_PIE",
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.POTATO), null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&r巧克力蛋糕", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "CHOCOLATE_CAKE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, new ItemStack(Material.EGG), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ=="), "&r奶油曲奇", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "CREAM_COOKIE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&r蓝莓松饼", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "BLUEBERRY_MUFFIN",
		new ItemStack[] {getItem("BLUEBERRY"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&r南瓜松饼", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "PUMPKIN_MUFFIN",
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&r巧克力松饼", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "CHOCOLATE_CHIP_MUFFIN",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ=="), "&r波士顿奶油馅饼", "", "&7&o恢复 &b&o" + "4.5" + " &7&o饱食度"), "BOSTON_CREAM_PIE",
		new ItemStack[] {null, getItem("CHOCOLATE_BAR"), null, null, SlimefunItems.HEAVY_CREAM, null, null, getItem("BISCUIT"), null},
		9)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&r热狗", "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), "HOT_DOG",
		new ItemStack[] {null, null, null, null, new ItemStack(Material.COOKED_PORKCHOP), null, null, new ItemStack(Material.BREAD), null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&r培根包裹奶酪填充热狗", "&7&o\"呼叫厨师\" - @Eyamaz", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "BACON_WRAPPED_CHEESE_FILLED_HOT_DOG",
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("CHEESE"), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&r烧烤培根包裹热狗", "&7&o\"想来点热狗吗?\" - @Pahimar", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "BBQ_BACON_WRAPPED_HOT_DOG",
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("BBQ_SAUCE"), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&r烧烤双培根包裹在玉米粉薄烙饼的热狗加乳酪", "&7&o\"呼叫厨师\" - @Eyamaz", "", "&7&o恢复 &b&o" + "10.0" + " &7&o饱食度"), "BBQ_DOUBLE_BACON_WRAPPED_HOT_DOG_IN_A_TORTILLA_WITH_CHEESE",
		new ItemStack[] {getItem("BACON"), getItem("BBQ_SAUCE"), getItem("BACON"), getItem("BACON"), new ItemStack(Material.COOKED_PORKCHOP), getItem("BACON"), getItem("CORNMEAL"), getItem("CHEESE"), getItem("CORNMEAL")},
		20)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhlOTRkZGQ3NjlhNWJlYTc0ODM3NmI0ZWM3MzgzZmQzNmQyNjc4OTRkN2MzYmVlMDExZThlNGY1ZmNkNyJ9fX0="), "&a加糖的茶", "", "&7&o恢复 &b&o" + "3.0" + " &7&o饱食度"), "SWEETENED_TEA",
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDExNTExYmRkNTViY2I4MjgwM2M4MDM5ZjFjMTU1ZmQ0MzA2MjYzNmUyM2Q0ZDQ2YzRkNzYxYzA0ZDIyYzIifX19"), "&6热可可", "", "&7&o恢复 &b&o" + "4.0" + " &7&o饱食度"), "HOT_CHOCOLATE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&6Pinacolada", "", "&7&o恢复 &b&o" + "7.0" + " &7&o饱食度"), "PINACOLADA",
		new ItemStack[] {getItem("PINEAPPLE"), getItem("ICE_CUBE"), getItem("COCONUT_MILK"), null, null, null, null, null, null},
		14)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.NETHER_WART, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ0ZWQ3YzczYWMyODUzZGZjYWE5Y2E3ODlmYjE4ZGExZDQ3YjE3YWQ2OGIyZGE3NDhkYmQxMWRlMWE0OWVmIn19fQ=="), "&c巧克力草莓", "", "&7&o恢复 &b&o" + "2.5" + " &7&o饱食度"), "CHOCOLATE_STRAWBERRY",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		5)
		.register();

		new Juice(category_drinks, new CustomPotion("&e柠檬汽水", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 8, 0), "", "&7&o恢复 &b&o" + "4.0" + " &7&o饱食度"), "LEMONADE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON_JUICE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&r番薯饼", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), "SWEET_POTATO_PIE",
		new ItemStack[] {getItem("SWEET_POTATO"), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, SlimefunItems.WHEAT_FLOUR, null, null, null, null, null},
		13);

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&rLamington", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "LAMINGTON",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("COCONUT"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&r威化饼", "", "&7&o恢复 &b&o" + "6.0" + " &7&o饱食度"), "WAFFLES",
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), getItem("BUTTER"), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r俱乐部三明治", "", "&7&o恢复 &b&o" + "9.5" + " &7&o饱食度"), "CLUB_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), getItem("BACON"), getItem("TOMATO"), getItem("LETTUCE"), getItem("MUSTARD"), null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19"), "&r卷饼", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "BURRITO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19"), "&r鸡肉卷饼", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "CHICKEN_BURRITO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_CHICKEN), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlZTg0ZDE5Yzg1YWZmNzk2Yzg4YWJkYTIxZWM0YzkyYzY1NWUyZDY3YjcyZTVlNzdiNWFhNWU5OWVkIn19fQ=="), "&r烤三明治", "", "&7&o恢复 &b&o" + "5.5" + " &7&o饱食度"), "GRILLED_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("CHEESE"), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDNhMzU3NGE4NDhmMzZhZTM3MTIxZTkwNThhYTYxYzEyYTI2MWVlNWEzNzE2ZjZkODI2OWUxMWUxOWUzNyJ9fX0="), "&r烤宽面条", "", "&7&o恢复 &b&o" + "8.5" + " &7&o饱食度"), "LASAGNA",
		new ItemStack[] {getItem("TOMATO"), getItem("CHEESE"), SlimefunItems.WHEAT_FLOUR, getItem("TOMATO"), getItem("CHEESE"), new ItemStack(Material.COOKED_BEEF), null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUzNjZjYTE3OTc0ODkyZTRmZDRjN2I5YjE4ZmViMTFmMDViYTJlYzQ3YWE1MDM1YzgxYTk1MzNiMjgifX19"), "&r冰淇淋", "", "&7&o恢复 &b&o" + "8.0" + " &7&o饱食度"), "ICE_CREAM",
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("ICE_CUBE"), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), getItem("STRAWBERRY"), null, null, null, null},
		16)
		.register();

		new Juice(category_drinks, new CustomPotion("&6菠萝汁", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o恢复 &b&o" + "3.0" + " &7&o饱食度"), "PINEAPPLE_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem("PINEAPPLE"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&6菠萝冰沙", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), "PINEAPPLE_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PINEAPPLE_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&r提拉米苏", "", "&7&o恢复 &b&o" + "8.0" + " &7&o饱食度"), "TIRAMISU",
		new ItemStack[] {getItem("HEAVY_CREAM"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), new ItemStack(Material.EGG), null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&r提拉米苏加草莓", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "TIRAMISU_WITH_STRAWBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&r提拉米苏加加树莓", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "TIRAMISU_WITH_RASPBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("RASPBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&r提拉米苏加加黑莓", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "TIRAMISU_WITH_BLACKBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("BLACKBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&r巧克力梨蛋糕", "", "&7&o恢复 &b&o" + "9.5" + " &7&o饱食度"), "CHOCOLATE_PEAR_CAKE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&c苹果梨蛋糕", "", "&7&o恢复 &b&o" + "9.0" + " &7&o饱食度"), "APPLE_PEAR_CAKE",
		new ItemStack[] {getItem("APPLE"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		18)
		.register();
	}

	@Override
	public void onDisable() {
		berries = null;
		trees = null;
		items = null;
	}

	public void registerTree(String name, Material material, String texture, String fruitName, String color, Color pcolor, String juice, boolean pie, Material... soil) {
		String id = name.toUpperCase().replace(" ", "_");
		Tree tree = new Tree(id, fruitName, texture, soil);
		trees.add(tree);

		items.put(id + "_SAPLING", new CustomItem(Material.OAK_SAPLING, color + name + " 树苗"));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + name + " 树苗"), id + "_SAPLING", new RecipeType(new CustomItem(Material.GRASS, "&7Breaking Grass")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		try {
			new EGPlant(category_main, new CustomItem(getSkull(material, texture), color + StringUtils.format(fruitName)), fruitName, new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Obtained by harvesting the specific Tree")), true,
			new ItemStack[] {null, null, null, null, getItem(id + "_SAPLING"), null, null, null, null})
			.register();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (pcolor != null) {
			new Juice(category_drinks, new CustomPotion(color + juice, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o恢复 &b&o" + "3.0" + " &7&o饱食度"), juice.toUpperCase().replace(" ", "_"), RecipeType.JUICER,
			new ItemStack[] {getItem(fruitName), null, null, null, null, null, null, null, null})
			.register();
		}

		if (pie) {
			try {
				new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), color + StringUtils.format(fruitName) + " Pie", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), fruitName + "_PIE",
				new ItemStack[] {getItem(fruitName), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
				13)
				.register();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!new File("plugins/ExoticGarden/schematics", id + "_TREE.schematic").exists())
			saveResource("schematics/" + id + "_TREE.schematic", false);
	}

	public void registerBerry(String name, String color, Color pcolor, PlantType type, PlantData data) {
		Berry berry = new Berry(name.toUpperCase(), type, data);
		berries.add(berry);

		items.put(name.toUpperCase() + "_BUSH", new CustomItem(Material.OAK_SAPLING, color + name + " 灌木"));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + name + " 灌木"), name.toUpperCase() + "_BUSH", new RecipeType(new CustomItem(Material.GRASS, "&7Breaking Grass")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(category_main, new CustomItem(getSkull(Material.NETHER_WART, data.getTexture()), color + name), name.toUpperCase(), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Obtained by harvesting the specific Bush")), true,
		new ItemStack[] {null, null, null, null, getItem(name.toUpperCase() + "_BUSH"), null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion(color + name + " 汁", pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o恢复 &b&o" + "3.0" + " &7&o饱食度"), name.toUpperCase() + "_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem(name.toUpperCase()), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion(color + name + " 冰沙", pcolor, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o恢复 &b&o" + "5.0" + " &7&o饱食度"), name.toUpperCase() + "_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem(name.toUpperCase() + "_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		try {
			new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4YTkzOTA5M2FiMWNkZTY2NzdmYWY3NDgxZjMxMWU1ZjE3ZjYzZDU4ODI1ZjBlMGMxNzQ2MzFmYjA0MzkifX19"), color + name + " 果冻三明治", "", "&7&o恢复 &b&o" + "8.0" + " &7&o饱食度"), name.toUpperCase() + "_JELLY_SANDWICH",
			new ItemStack[] {null, new ItemStack(Material.BREAD), null, null, getItem(name.toUpperCase() + "_JUICE"), null, null, new ItemStack(Material.BREAD), null},
			16)
			.register();

			new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), color + name + " 派", "", "&7&o恢复 &b&o" + "6.5" + " &7&o饱食度"), name.toUpperCase() + "_PIE",
			new ItemStack[] {getItem(name.toUpperCase()), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
			13)
			.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ItemStack getItem(String id) {
		SlimefunItem item = SlimefunItem.getByID(id);
		return item != null ? item.getItem(): null;
	}

	public static ItemStack getSkull(Material material, String texture) {
		try {
			return skullitems && !texture.equals("NO_SKULL_SPECIFIED") ? CustomSkull.getItem(texture) : new ItemStack(material);
		} catch (Exception e) {
			e.printStackTrace();
			return new ItemStack(material);
		}
	}

	public void registerPlant(String name, String color, Material material, PlantType type, PlantData data) {
		Berry berry = new Berry(name.toUpperCase().replace(" ", "_"), type, data);
		berries.add(berry);

		items.put(name.toUpperCase() + "_BUSH", new CustomItem(Material.OAK_SAPLING, color + name + " 植物"));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + name + " 植物"), name.toUpperCase().replace(" ", "_") + "_BUSH", new RecipeType(new CustomItem(Material.GRASS, "&7打破草")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(category_main, new CustomItem(getSkull(material, data.getTexture()), color + name), name.toUpperCase().replace(" ", "_"), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7通过收获特定的灌木获得")), true,
		new ItemStack[] {null, null, null, null, getItem(name.toUpperCase().replace(" ", "_") + "_BUSH"), null, null, null, null})
		.register();
	}

	public void registerMagicalPlant(String name, ItemStack item, String skull, ItemStack[] recipe) {
		ItemStack essence = new CustomItem(Material.BLAZE_POWDER, "&rMagical Essence", "", "&7" + name);

		Berry berry = new Berry(essence, name.toUpperCase() + "_ESSENCE", PlantType.ORE_PLANT, new PlantData(skull));
		berries.add(berry);

		new SlimefunItem(category_magic, new CustomItem(Material.OAK_SAPLING, "&r" + name + " 魔法植物"), name.toUpperCase().replace(" ", "_") + "_PLANT", RecipeType.ENHANCED_CRAFTING_TABLE,
		recipe)
		.register();

		HandledBlock plant = new HandledBlock(category_magic, essence, name.toUpperCase().replace(" ", "_") + "_ESSENCE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {essence, essence, essence, essence, null, essence, essence, essence, essence});

		plant.setRecipeOutput(item.clone());
		plant.register();
	}

	public static Berry getBerry(Block block) {
		SlimefunItem item = BlockStorage.check(block);
		if (item != null && item instanceof HandledBlock) {
			for (Berry berry : ExoticGarden.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) return berry;
			}
		}
		return null;
	}

	public static ItemStack harvestPlant(Block block) {
		ItemStack itemstack = null;
		SlimefunItem item = BlockStorage.check(block);
		if (item != null) {
			for (Berry berry : berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) {
					switch (berry.getType()) {
						case ORE_PLANT:
						case DOUBLE_PLANT: {
							Block plant;
							if (BlockStorage.check(block.getRelative(BlockFace.DOWN)) == null) {
								plant = block;
								BlockStorage.retrieve(block.getRelative(BlockFace.UP));
								block.getWorld().playEffect(block.getRelative(BlockFace.UP).getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								logRemoveal(Bukkit.getOfflinePlayer(UUID.fromString(BlockStorage.getLocationInfo(block.getLocation(),"owner"))).getName()
										,block.getRelative(BlockFace.UP).getLocation()
										,block.getRelative(BlockFace.UP).getType()
										,block.getRelative(BlockFace.UP).getBlockData());
								block.getRelative(BlockFace.UP).setType(Material.AIR);
							}
							else {
								plant = block.getRelative(BlockFace.DOWN);
								BlockStorage.retrieve(block);
								block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								logRemoveal(Bukkit.getOfflinePlayer(UUID.fromString(BlockStorage.getLocationInfo(block.getLocation(),"owner"))).getName()
										,block.getLocation()
										,block.getType()
										,block.getBlockData());
								block.setType(Material.AIR);

							}
							plant.setType(Material.OAK_SAPLING);
							logPlacement(Bukkit.getOfflinePlayer(UUID.fromString(BlockStorage.getLocationInfo(block.getLocation(),"owner"))).getName()
									,block.getLocation()
									,block.getType()
									,block.getBlockData());
							itemstack = berry.getItem();
							BlockStorage.store(plant, getItem(berry.toBush()));
							break;
						}
						default: {
							block.setType(Material.OAK_SAPLING);
							logPlacement(Bukkit.getOfflinePlayer(UUID.fromString(BlockStorage.getLocationInfo(block.getLocation(),"owner"))).getName()
									,block.getLocation()
									,block.getType()
									,block.getBlockData());
							itemstack = berry.getItem();
							BlockStorage.store(block, getItem(berry.toBush()));
							break;
						}
					}
				}
			}
		}
		return itemstack;
	}
	public static boolean logRemoveal(String user, Location location, Material type, BlockData blockData){
		if(coreProtectAPI == null)
			return false;
		return coreProtectAPI.logRemoval(user,location,type,blockData);
	}
	public static boolean logPlacement(String user, Location location, Material type, BlockData blockData){
		if(coreProtectAPI == null)
			return false;
		return coreProtectAPI.logPlacement(user,location,type,blockData);
	}
	public static boolean logInteraction(String user, Location location){
		if(coreProtectAPI == null)
			return false;
		return coreProtectAPI.logInteraction(user,location);
	}
	public static boolean logContainerTransaction(String user, Location location){
		if(coreProtectAPI == null)
			return false;
		return coreProtectAPI.logContainerTransaction(user,location);
	}
}
