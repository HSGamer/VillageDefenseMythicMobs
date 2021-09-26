package me.hsgamer.villagedefensemythicmobs.spawner;

import com.udojava.evalex.Expression;
import me.hsgamer.villagedefensemythicmobs.config.SpawnerData;
import me.hsgamer.villagedefensemythicmobs.hook.mythicmobs.MythicMobSpawner;
import org.bukkit.Location;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;
import plugily.projects.villagedefense.arena.options.ArenaOption;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public abstract class AbstractMythicSpawner implements EnemySpawner {
    protected static final Logger LOGGER = Logger.getLogger(MythicMobSpawner.class.getSimpleName());

    private static final String WAVE_VAR = "wave";
    private static final String PHASE_VAR = "phase";
    private static final String AMOUNT_VAR = "amount";
    private static final String LEFT_VAR = "left";
    private static final String ENEMY_VAR = "enemy";
    private static final String VILLAGER_VAR = "villager";
    private static final String WOLF_VAR = "wolf";
    private static final String GOLEM_VAR = "golem";

    private final String spawnerName;
    private final String mobName;
    private final int priority;
    private final List<Expression> phaseConditions = new ArrayList<>();
    private final List<Expression> waveConditions = new ArrayList<>();
    private Expression spawnRateExpression = new Expression("0");
    private Expression finalAmountExpression = new Expression("0");
    private Expression spawnWeightExpression = new Expression("1");
    private Expression levelExpression = new Expression("1");

    protected AbstractMythicSpawner(SpawnerData spawnerData) {
        this.spawnerName = spawnerData.spawnerName;
        this.mobName = spawnerData.mobName;
        this.priority = spawnerData.priority;
    }

    private static void applyVariables(Expression expression, Arena arena, int wave, int phase, int spawnAmount) {
        expression.setVariable(WAVE_VAR, BigDecimal.valueOf(wave));
        expression.setVariable(PHASE_VAR, BigDecimal.valueOf(phase));
        expression.setVariable(AMOUNT_VAR, BigDecimal.valueOf(spawnAmount));
        expression.setVariable(LEFT_VAR, BigDecimal.valueOf(arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN)));
        expression.setVariable(ENEMY_VAR, BigDecimal.valueOf(arena.getEnemies().size()));
        expression.setVariable(VILLAGER_VAR, BigDecimal.valueOf(arena.getVillagers().size()));
        expression.setVariable(WOLF_VAR, BigDecimal.valueOf(arena.getWolves().size()));
        expression.setVariable(GOLEM_VAR, BigDecimal.valueOf(arena.getIronGolems().size()));
    }

    public void addPhaseConditions(List<Expression> phaseConditions) {
        this.phaseConditions.addAll(phaseConditions);
    }

    public void addWaveConditions(List<Expression> waveConditions) {
        this.waveConditions.addAll(waveConditions);
    }

    public void setSpawnRateExpression(Expression spawnRateExpression) {
        this.spawnRateExpression = spawnRateExpression;
    }

    public void setFinalAmountExpression(Expression finalAmountExpression) {
        this.finalAmountExpression = finalAmountExpression;
    }

    public void setSpawnWeightExpression(Expression spawnWeightExpression) {
        this.spawnWeightExpression = spawnWeightExpression;
    }

    public void setLevelExpression(Expression levelExpression) {
        this.levelExpression = levelExpression;
    }

    private double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
        applyVariables(spawnRateExpression, arena, wave, phase, spawnAmount);
        return spawnRateExpression.eval().doubleValue();
    }

    private int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
        applyVariables(finalAmountExpression, arena, wave, phase, spawnAmount);
        return finalAmountExpression.eval().intValue();
    }

    private int getSpawnWeight(Arena arena, int wave, int phase, int spawnAmount) {
        applyVariables(spawnWeightExpression, arena, wave, phase, spawnAmount);
        return spawnWeightExpression.eval().intValue();
    }

    private double getLevel(Arena arena, int wave, int phase, int spawnAmount) {
        applyVariables(levelExpression, arena, wave, phase, spawnAmount);
        return levelExpression.eval().doubleValue();
    }

    private boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
        return phaseConditions.parallelStream().anyMatch(expression -> {
            applyVariables(expression, arena, wave, phase, spawnAmount);
            return !expression.eval().equals(BigDecimal.ZERO);
        });
    }

    private boolean checkWave(int wave) {
        return waveConditions.parallelStream().anyMatch(expression -> {
            expression.setVariable(WAVE_VAR, BigDecimal.valueOf(wave));
            return !expression.eval().equals(BigDecimal.ZERO);
        });
    }

    @Override
    public String getName() {
        return "MythicSpawner_" + spawnerName;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public String getMobName() {
        return mobName;
    }

    public String getSpawnerName() {
        return spawnerName;
    }

    protected abstract boolean spawn(Location location, Arena arena, double level);

    @Override
    public void spawn(Random random, Arena arena, int spawn) {
        int wave = arena.getWave();
        int phase = arena.getOption(ArenaOption.ZOMBIE_SPAWN_COUNTER);
        if (this.checkPhase(arena, wave, phase, spawn) && this.checkWave(wave)) {
            int spawnAmount = this.getFinalAmount(arena, wave, phase, spawn);
            double spawnRate = this.getSpawnRate(arena, wave, phase, spawn);
            int weight = this.getSpawnWeight(arena, wave, phase, spawn);
            double level = this.getLevel(arena, wave, phase, spawn);

            for (int i = 0; i < spawnAmount; ++i) {
                int zombiesToSpawn = arena.getOption(ArenaOption.ZOMBIES_TO_SPAWN);
                if (zombiesToSpawn >= weight && spawnRate != 0.0D && (spawnRate == 1.0D || random.nextDouble() < spawnRate)) {
                    Location location = arena.getRandomZombieSpawn(random);
                    if (this.spawn(location, arena, level)) {
                        arena.setOptionValue(ArenaOption.ZOMBIES_TO_SPAWN, zombiesToSpawn - weight);
                    }
                }
            }
        }
    }
}
