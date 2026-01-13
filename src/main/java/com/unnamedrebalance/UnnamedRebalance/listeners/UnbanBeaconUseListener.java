package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;

import java.util.*;

public class UnbanBeaconUseListener implements Listener {

    private static final String GUI_TITLE = "§6§lBeacon of Resurrection";

    private final NamespacedKey unbanBeaconKey;
    private final NamespacedKey bannedPlayerKey;

    public UnbanBeaconUseListener(UnnamedRebalance plugin) {
        this.unbanBeaconKey = new NamespacedKey(plugin, "unban_beacon");
        this.bannedPlayerKey = new NamespacedKey(plugin, "banned_player");
    }

    /* ------------------------------------------------------------ */
    @EventHandler
    public void onUnbanBeaconUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
            event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.BEACON || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (!meta.getPersistentDataContainer().has(unbanBeaconKey, PersistentDataType.BYTE)) return;

        event.setCancelled(true);
        openUnbanGUI(player);
    }

    /* ------------------------------------------------------------ */
    private void openUnbanGUI(Player player) {
        BanList banList = Bukkit.getBanList(BanList.Type.PROFILE);

        Set<PlayerProfile> profiles = new HashSet<>();
        for (Object obj : banList.getEntries()) {
            if (!(obj instanceof BanEntry entry)) continue;
        
            Object target = entry.getBanTarget();
            if (target instanceof PlayerProfile profile) {
                profiles.add(profile);
            }
        }

        if (profiles.isEmpty()) {
            player.sendMessage("§c§lNo players are currently eliminated!");
            return;
        }

        int size = Math.min(54, ((profiles.size() + 8) / 9) * 9);
        Inventory gui = Bukkit.createInventory(null, size, GUI_TITLE);

        int slot = 0;
        for (PlayerProfile profile : profiles) {
            if (slot >= size) break;
            gui.setItem(slot++, createBannedPlayerHead(profile, banList));
        }

        player.openInventory(gui);
    }

    /* ------------------------------------------------------------ */
    private ItemStack createBannedPlayerHead(PlayerProfile profile, BanList banList) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return skull;

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(profile.getUniqueId()));
        meta.setDisplayName("§c§l" + profile.getName());

        List<String> lore = new ArrayList<>();
        lore.add("§7Status: §cEliminated");

        BanEntry entry = banList.getBanEntry(profile);
        if (entry != null && entry.getReason() != null && !entry.getReason().isEmpty()) {
            lore.add("§7Reason: §f" + entry.getReason());
        }

        lore.add("");
        lore.add("§e§lClick to resurrect this player!");

        meta.setLore(lore);
        meta.getPersistentDataContainer().set(
                bannedPlayerKey,
                PersistentDataType.STRING,
                profile.getUniqueId().toString()
        );

        skull.setItemMeta(meta);
        return skull;
    }

    /* ------------------------------------------------------------ */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) return;

        String uuidString = meta.getPersistentDataContainer()
                .get(bannedPlayerKey, PersistentDataType.STRING);

        if (uuidString == null) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() != Material.BEACON || !mainHand.hasItemMeta()) {
            player.sendMessage("§c§lYou no longer have the Beacon of Resurrection!");
            player.closeInventory();
            return;
        }

        ItemMeta beaconMeta = mainHand.getItemMeta();
        if (beaconMeta == null ||
            !beaconMeta.getPersistentDataContainer().has(unbanBeaconKey, PersistentDataType.BYTE)) {
            player.sendMessage("§c§lYou no longer have the Beacon of Resurrection!");
            player.closeInventory();
            return;
        }

        PlayerProfile profile = Bukkit.createProfile(UUID.fromString(uuidString));
        BanList banList = Bukkit.getBanList(BanList.Type.PROFILE);
        banList.pardon(profile);

        OfflinePlayer offline = Bukkit.getOfflinePlayer(profile.getUniqueId());
        if (offline.isOnline()) {
            Player online = offline.getPlayer();
            if (online != null) {
                AttributeInstance health = online.getAttribute(Attribute.MAX_HEALTH);
                if (health != null) {
                    health.setBaseValue(20.0);
                    online.setHealth(20.0);
                }
            }
        }

        mainHand.setAmount(mainHand.getAmount() - 1);
        player.closeInventory();

        player.sendMessage("§a§lYou have resurrected §e" + profile.getName() + "§a§l!");
        Bukkit.broadcastMessage("§6§l✦ §e" + profile.getName()
                + " §6has been resurrected by §e" + player.getName() + "§6!");

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
        player.spawnParticle(
                Particle.TOTEM_OF_UNDYING,
                player.getLocation().add(0, 1, 0),
                30,
                0.5, 0.5, 0.5,
                0.1
        );
    }
}
