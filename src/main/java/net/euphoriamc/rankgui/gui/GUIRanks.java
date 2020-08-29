package net.euphoriamc.rankgui.gui;

import lombok.Getter;
import lombok.Setter;
import net.euphoriamc.rankgui.RankGUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.awt.Color;
import java.util.*;

//TODO make sure to add the placeholder for the amount of votes and money you have. Probably going to have to be inside
// its own method and an update method or something like that. So that way there is a player variable.
public class GUIRanks implements InventoryHolder, Listener {

    private final Inventory inventory;
    private static final ArrayList<PrestigePage> prestigePages = new ArrayList<>();

    private static final ItemStack filler = createItem(Material.MAGENTA_STAINED_GLASS_PANE, " ", " ");
    private static final ItemStack backButton = createItem(Material.ARROW, ChatColor.WHITE + "Back", ChatColor.WHITE + "Click to go back a page.");
    private static final ItemStack nextButton = createItem(Material.ARROW, ChatColor.WHITE + "Next", ChatColor.WHITE + "Click to go to the next page.");
    private static final ItemStack cancelButton = createItem(Material.BARRIER, ChatColor.RED + "Cancel", ChatColor.RED + "Click to leave.");
    private static final ItemStack nextPrestige = createItem(Material.GOLD_BLOCK, ChatColor.WHITE + "Next Prestige Page", ChatColor.WHITE + "Click to view the next prestige page.");
    private static final ItemStack backPrestige = createItem(Material.REDSTONE_BLOCK, ChatColor.WHITE + "Previous Prestige Page", ChatColor.WHITE + "Click to view the previous prestige page.");

    private static ItemStack[] base1 = null;
    private static ItemStack[] base2 = null;
    private static ItemStack[] base3 = null;

    private static final ChatColor[] colors = {ChatColor.WHITE, ChatColor.of(new Color(255, 140, 0)), ChatColor.DARK_PURPLE,
            ChatColor.BLUE, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY, ChatColor.DARK_GRAY,
            ChatColor.DARK_BLUE, ChatColor.of(new Color(170, 0, 255)), ChatColor.of(new Color(0, 0, 255)),
            ChatColor.of(new Color(210, 105, 30)), ChatColor.DARK_GREEN};

    private static final Material[] materialColors = {Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.MAGENTA_CONCRETE,
            Material.LIGHT_BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.PINK_CONCRETE, Material.GRAY_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE, Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE, Material.BROWN_CONCRETE,
            Material.GREEN_CONCRETE};

    @Getter
    @Setter
    private int prestige = 0;
    @Getter
    @Setter
    private int page = 0;
    @Getter
    @Setter
    private Player p;
    @Getter
    @Setter
    private boolean isCancelled = false;

    static {
        initializePrestige();
    }

    public GUIRanks() {
        this.inventory = null;
    }

    public GUIRanks(Player p) {
        this.p = p;
        inventory = Bukkit.createInventory(this, 54,
                ChatColor.GRAY + "[" + ChatColor.BOLD + ChatColor.of(new Color(255, 0, 255))
                        + "EuphoriaRanks" + ChatColor.GRAY + "]");
        initializeItems();
    }

    public void openInventory() {
        p.openInventory(inventory);
    }

    private void initializeItems() {
        System.out.println("fuck");
        prestigePages.forEach(s -> System.out.println(s + "\n"));
        System.out.println("you");
        for (PrestigePage page : prestigePages) {
            System.out.println("ADD SPACE HERE");
            for (ItemStack[] array : page.getPages()) {
                System.out.println(Arrays.toString(array));
            }
        }
        /*prestigePages.forEach(s -> s.getPages().forEach(t ->
                System.out.println(Arrays.toString(t) + "\n")));*/
        PrestigePage prestigePage = prestigePages.get(prestige);
        ItemStack[] page = prestigePage.getPages().get(this.page);

        updatePlaceholders(page);
        inventory.setContents(page);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof GUIRanks))
            return;
        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null)
            return;
        if (!isButton(clickedItem))
            return;

        updatePages((GUIRanks) e.getInventory().getHolder(), getButtonType(clickedItem));
    }

    private void updatePages(GUIRanks holder, int update) {
        if (update == 0) {
            prestige -= 1;
        } else if (update == 1) {
            page -= 1;
        } else if (update == 2) {
            isCancelled = true;
        } else if (update == 3) {
            page += 1;
        } else if (update == 4) {
            prestige += 1;
        }
        updatePage(holder);
    }

    private void updatePage(GUIRanks holder) {

        if (isCancelled)
            holder.p.closeInventory();
        PrestigePage prestige = prestigePages.get(this.prestige);
        if (prestige == null)
            return;
        ItemStack[] page = prestige.getPages().get(this.page);

        holder.inventory.setContents(page);
        holder.p.updateInventory();
    }

    private boolean isButton(ItemStack toTest) {
        return toTest.isSimilar(backButton) || toTest.isSimilar(backPrestige) || toTest.isSimilar(cancelButton)
                || toTest.isSimilar(nextButton) || toTest.isSimilar(nextPrestige);
    }

    /*
    0 = backPrestige
    1 = backButton
    2 = cancelButton
    3 = nextPage
    4 = nextPrestige
     */
    private int getButtonType(ItemStack toTest) {
        if (toTest.isSimilar(backPrestige))
            return 0;
        else if (toTest.isSimilar(backButton))
            return 1;
        else if (toTest.isSimilar(cancelButton))
            return 2;
        else if (toTest.isSimilar(nextButton))
            return 3;
        else if (toTest.isSimilar(nextPrestige))
            return 4;
        else return 2;
    }

    private void updatePlaceholders(ItemStack[] page) {
        for (ItemStack item : page) {
            if (item == null) {
                continue;
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null)
                continue;
            List<String> lore = itemMeta.getLore();
            ArrayList<String> newLore = new ArrayList<>();
            if (lore == null)
                continue;
            lore.forEach(s -> {
                s = s.replaceAll("%votes%", "placeholder value");
                s = s.replaceAll("%balance%", "placeholder value");
                newLore.add(s);
            });
            itemMeta.setLore(newLore);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private static ItemStack createItem(int level) {
        int index = level % 14;
        ChatColor color = colors[index];
        String name = color + "Rank " + level;

        ArrayList<String> lore = new ArrayList<>();
        String[] loreHolder = new String[1];

        lore.add("Placeholder 1");

        return createItem(materialColors[index], name, lore.toArray(lore.toArray(loreHolder)));
    }

    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack itemStack = new ItemStack(material);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return itemStack;
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));
        itemMeta.addEnchant(Enchantment.LUCK, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private static void setLore(ItemStack item, String... loreList) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null)
            return;

        List<String> lore = Arrays.asList(loreList);
        itemMeta.setLore(lore);

        item.setItemMeta(itemMeta);
    }

    /**
     * Get the base gui for each page.
     *
     * @param gate int 1-3 1 being first page 3 being last page
     * @return base gui
     */
    private static ItemStack[] getBase(int gate) {
        if (base1 == null || base2 == null || base3 == null) {

            base1 = new ItemStack[54];
            base2 = new ItemStack[54];
            base3 = new ItemStack[54];

            for (int i = 0; i < 9; i++) {
                base1[i] = filler;
                base2[i] = filler;
                base3[i] = filler;

                int index = i + 45;
                base1[index] = filler;
                base2[index] = filler;
                base3[index] = filler;
            }

            for (int i = 1; i < 5; i++) {
                int index = i * 9;
                base1[index] = filler;
                base2[index] = filler;
                base3[index] = filler;

                index += 8;
                base1[index] = filler;
                base2[index] = filler;
                base3[index] = filler;
            }

            base1[49] = cancelButton;
            base2[49] = cancelButton;
            base3[49] = cancelButton;

            base1[52] = nextButton;
            //base1[53] = nextPrestige;
            base2[52] = nextButton;
            //base2[53] = nextPrestige;

            //base3[45] = backPrestige;
            base3[46] = backButton;
            //base2[45] = backPrestige;
            base2[46] = backButton;
        }
        switch (gate) {
            case 3:
                return base3.clone();
            case 2:
                return base2.clone();
            default:
                return base1.clone();
        }
    }

    private static void initializePrestige() {
        RankGUI plugin = RankGUI.getInstance();
        FileConfiguration config = plugin.getConfig();

        ConfigurationSection section = config.getConfigurationSection("ranks");
        if (section == null)
            return;
        Stack<String> keys = new Stack<>();
        keys.addAll(section.getKeys(false));

        prestigePages.add(new PrestigePage(keys.pop(), keys.size() == 1 ? -1 : 0).initializePages());

        int limit = keys.size();
        for (int i = 1; i < limit; i++)
            prestigePages.add(new PrestigePage(keys.pop(), 1).initializePages());
        if (keys.size() != 0)
            prestigePages.add(new PrestigePage(keys.pop(), 2).initializePages());
    }

    public static class PrestigePage {
        private final String key;
        /*
        -1 for single prestige
        0 for first prestige
        1 for middle prestige
        2 for last prestige
         */
        private final int place;

        @Getter
        private final ArrayList<ItemStack[]> pages = new ArrayList<>();

        public PrestigePage(String key, int place) {
            this.key = key;
            this.place = place;
        }

        //base1[53] = nextPrestige;
        //base2[53] = nextPrestige;
        //base3[45] = backPrestige;
        //base2[45] = backPrestige;

        /*
        0    1  2  3  4  5  6  7    8
             29
        9    10 11 12 13 14 15 16   17
        18   19 20 21 22 23 24 25   26
        27   28 29 30 31 32 33 34   35
        36   37 38 39 40 41 42 43   44

        45   46 47 48 49 50 51 52   53
         */

        private final int[] rankIndexes = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43};

        public PrestigePage initializePages() {
            RankGUI plugin = RankGUI.getInstance();
            FileConfiguration config = plugin.getConfig();

            Stack<String> ranks = new Stack<>();
            ranks.addAll(config.getStringList("ranks." + key));

            int maxPages = ranks.size() % 28 == 0 ? ranks.size() / 28 : ranks.size() / 28 + 1;

            if (maxPages != 0) {
                ItemStack[] firstPage = getBase(1);
                setPrestigeIndexes(firstPage);
                getPage(ranks, firstPage, 0);

                for (int i = 1; i < maxPages; i++) {
                    ItemStack[] page = getBase(2);
                    setPrestigeIndexes(page);
                    getPage(ranks, page, i);
                }

                ItemStack[] page = getBase(3);
                setPrestigeIndexes(page);
                getPage(ranks, page, maxPages - 1);
                return this;
            }

            ItemStack[] page = getBase(1);
            setPrestigeIndexes(page);
            page[52] = filler;
            getPage(ranks, page, 0);
            return this;
        }

        private void setPrestigeIndexes(ItemStack[] page) {
            if (place == 0) {
                page[53] = nextPrestige;
            } else if (place == 1) {
                page[45] = backPrestige;
                page[53] = nextPrestige;
            } else if (place == 2) {
                page[45] = backPrestige;
            }
        }

        private void getPage(Stack<String> ranks, ItemStack[] page, int offset) {
            for (int i = 0; i < ranks.size(); i++) {
                if (i == 27)
                    break;

                int index = rankIndexes[i];
                ItemStack rank = createItem(offset * 28 + i + 1);

                String[] loreLines = ranks.pop().split(",");
                getLoreLines(loreLines);
                setLore(rank, loreLines);

                page[index] = rank;
                pages.add(page);
            }
        }

        private void getLoreLines(String[] loreLines) {
            Arrays.stream(loreLines).forEach(s -> {
                ChatColor.translateAlternateColorCodes('&', s);
                ArrayList<String> colors = new ArrayList<>();

                boolean found = false;
                StringBuilder builder = new StringBuilder();
                for (char c : s.toCharArray()) {
                    if (c == '<' ) {
                        found = true;
                        continue;
                    } else if (c == '>') {
                        found = false;
                        colors.add(builder.toString());
                        builder = new StringBuilder();
                        continue;
                    }

                    if (found)
                        builder.append(c);
                }

                for (String colorString : colors) {
                    Color color = Color.decode(colorString);

                    s = s.replace("<" + colorString + ">", ChatColor.of(color) + "");
                }
            });
        }
    }
}