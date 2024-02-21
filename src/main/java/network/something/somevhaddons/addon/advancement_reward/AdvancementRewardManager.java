package network.something.somevhaddons.addon.advancement_reward;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import iskallia.vault.gear.trinket.TrinketHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.Mod;
import network.something.somevhaddons.SomeVHAddons;

import javax.annotation.Nullable;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.Util.NIL_UUID;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AdvancementRewardManager {

    //    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            var progress = getProgress(serverPlayer);

            var scoreboard = serverPlayer.getScoreboard();
            var objective = scoreboard.getOrCreateObjective("all_advancement_progress");
            var score = scoreboard.getOrCreatePlayerScore(serverPlayer.getScoreboardName(), objective);
            score.setScore(progress);

            if (progress < 100) {
                var message = new TextComponent("");
                message.append(new TextComponent("Twój postęp w zdobywaniu osiągnięć wynosi: "));
                message.append(new TextComponent(progress + "%").withStyle(ChatFormatting.GREEN));
                serverPlayer.sendMessage(message, ChatType.CHAT, NIL_UUID);
                return;
            }

            var message = new TextComponent("");
            message.append(serverPlayer.getDisplayName());
            message.append(" zdobył wszystkie osiągnięcia! Gratulacje!");
            serverPlayer.server.getPlayerList().broadcastMessage(message, ChatType.CHAT, NIL_UUID);
        }
    }

    /**
     * Player can be rewarded if wearing a full set of diamond armor
     *
     * @return null if player can be rewarded, otherwise a message why not
     */
    public static @Nullable String canReward(ServerPlayer player, Advancement advancement) {
        var isDiamondSet = player.getInventory().getArmor(0).is(Items.DIAMOND_BOOTS)
                && player.getInventory().getArmor(1).is(Items.DIAMOND_LEGGINGS)
                && player.getInventory().getArmor(2).is(Items.DIAMOND_CHESTPLATE)
                && player.getInventory().getArmor(3).is(Items.DIAMOND_HELMET);

        if (!isDiamondSet) {
            return "Musisz nosić komplet diamentowej zbroi";
        }

        var hasTrinkets = !TrinketHelper.getTrinkets(player).isEmpty();
        if (hasTrinkets) {
            return "Musisz zdjąć wszystkie trinkety";
        }

        var shieldInOffhand = player.getOffhandItem().is(Items.SHIELD);
        if (!shieldInOffhand) {
            return "Musisz trzymać tarczę (vanilla!) w drugiej ręce";
        }

        var diamondSword = player.getMainHandItem().is(Items.DIAMOND_SWORD);
        if (!diamondSword) {
            return "Musisz trzymać diamentowy miecz w ręce";
        }

        return null;
    }

    /**
     * Return progress of advancements in scale of 0-100
     */
    public static int getProgress(ServerPlayer player) {
        var toComplete = getAdvancements(player.server);

        var completed = 0;

        for (var advancement : player.server.getAdvancements().getAllAdvancements()) {
            var id = advancement.getId().toString();
            if (!toComplete.contains(id)) continue;

            if (player.getAdvancements().getOrStartProgress(advancement).isDone()) {
                completed++;
            }
        }

        var progress = (int) ((completed / (double) toComplete.size()) * 100);
        SomeVHAddons.LOGGER.info("Player " + player.getName().getString() + " has " + progress + "% of advancements");
        return progress;
    }

    public static List<String> getAdvancements(MinecraftServer server) {
        var path = server.getWorldPath(LevelResource.ROOT)
                .resolve("serverconfig")
                .resolve("all_advancements_reward.json");
        try {
            var reader = new FileReader(path.toString());
            var json = new GsonBuilder().create().fromJson(reader, JsonArray.class);

            var result = new ArrayList<String>();
            for (var element : json) {
                result.add(element.getAsString());
            }
            return result;
        } catch (Exception e) {
            SomeVHAddons.LOGGER.error("Error reading advancements", e);
            return new ArrayList<>();
        }
    }

}
