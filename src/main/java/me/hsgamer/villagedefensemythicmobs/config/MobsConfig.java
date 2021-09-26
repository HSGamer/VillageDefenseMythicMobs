package me.hsgamer.villagedefensemythicmobs.config;

import com.udojava.evalex.Expression;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.villagedefensemythicmobs.VillageDefenseMythicMobs;
import me.hsgamer.villagedefensemythicmobs.spawner.AbstractMythicSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MobsConfig {
    private final VillageDefenseMythicMobs plugin;
    private final BukkitConfig config;
    private final List<AbstractMythicSpawner> spawnerList = new ArrayList<>();

    public MobsConfig(VillageDefenseMythicMobs plugin) {
        this.plugin = plugin;
        this.config = new BukkitConfig(plugin, "mobs.yml");
    }

    public void setup() {
        config.setup();
    }

    public void reload() {
        clearSpawners();
        config.reload();
        initSpawners();
    }

    public void initSpawners() {
        config.getKeys(false).forEach(key -> {
            Map<String, Object> values = config.getNormalizedValues(key, false);
            getSpawner(key, values).ifPresent(spawner -> {
                spawnerList.add(spawner);
                plugin.getParentPlugin().getEnemySpawnerRegistry().getEnemySpawnerSet().add(spawner);
            });
        });
    }

    private Optional<AbstractMythicSpawner> getSpawner(String spawnerName, Map<String, Object> values) {
        int priority = Optional.ofNullable(values.get("priority")).map(String::valueOf).map(Integer::parseInt).orElse(0);
        Optional<String> mobName = Optional.ofNullable(values.get("name")).map(String::valueOf);
        Optional<String> type = Optional.ofNullable(values.get("type")).map(String::valueOf);
        if (!type.isPresent()) {
            plugin.getLogger().warning(() -> "The spawner '" + spawnerName + "' is missing a 'type' value");
            return Optional.empty();
        }
        if (!mobName.isPresent()) {
            plugin.getLogger().warning(() -> "The spawner '" + spawnerName + "' is missing a 'name' value");
            return Optional.empty();
        }
        SpawnerData spawnerData = new SpawnerData(spawnerName, mobName.get(), priority);
        Optional<AbstractMythicSpawner> optionalSpawner = plugin.getMythicSpawnerBuilder().build(type.get(), spawnerData);
        if (!optionalSpawner.isPresent()) {
            plugin.getLogger().warning(() -> "Unknown spawner type for the spawner '" + spawnerName + "'");
            return Optional.empty();
        }
        AbstractMythicSpawner spawner = optionalSpawner.get();

        Optional.ofNullable(values.get("phase-condition"))
                .map(o -> values.getOrDefault("phase", o))
                .map(o -> CollectionUtils.createStringListFromObject(o, true))
                .map(list -> list.stream().map(Expression::new).map(this::applyCustomFunction).collect(Collectors.toList()))
                .ifPresent(spawner::addPhaseConditions);
        Optional.ofNullable(values.get("wave-condition"))
                .map(o -> values.getOrDefault("wave", o))
                .map(o -> CollectionUtils.createStringListFromObject(o, true))
                .map(list -> list.stream().map(Expression::new).map(this::applyCustomFunction).collect(Collectors.toList()))
                .ifPresent(spawner::addWaveConditions);
        Optional.ofNullable(values.get("spawn-rate"))
                .map(o -> values.getOrDefault("rate", o))
                .map(String::valueOf)
                .map(Expression::new)
                .map(this::applyCustomFunction)
                .ifPresent(spawner::setSpawnRateExpression);
        Optional.ofNullable(values.get("final-amount"))
                .map(o -> values.getOrDefault("amount", o))
                .map(String::valueOf)
                .map(Expression::new)
                .map(this::applyCustomFunction)
                .ifPresent(spawner::setFinalAmountExpression);
        Optional.ofNullable(values.get("spawn-weight"))
                .map(o -> values.getOrDefault("weight", o))
                .map(String::valueOf)
                .map(Expression::new)
                .map(this::applyCustomFunction)
                .ifPresent(spawner::setSpawnWeightExpression);
        Optional.ofNullable(values.get("level"))
                .map(String::valueOf)
                .map(Expression::new)
                .map(this::applyCustomFunction)
                .ifPresent(spawner::setLevelExpression);

        return Optional.of(spawner);
    }

    public void clearSpawners() {
        spawnerList.forEach(plugin.getParentPlugin().getEnemySpawnerRegistry().getEnemySpawnerSet()::remove);
        spawnerList.clear();
    }

    private Expression applyCustomFunction(Expression expression) {
        ExpressionUtils.applyLazyFunction(expression);
        ExpressionUtils.applyLazyOperator(expression);
        return expression;
    }
}
