package it.vamxneedev.strivelib.database;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public abstract class DatabaseScheduler {

    private final Plugin instance;
    private final ArrayList<Runnable> runnables = new ArrayList<>();
    private Boolean processing = false;
    private BukkitTask schedulerTask;

    public DatabaseScheduler(Plugin plugin) {
        this.instance = plugin;
        this.startScheduler();
    }

    public BukkitTask startScheduler() {
        this.schedulerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!processing) {
                    processing = true;
                    ArrayList<Runnable> copiedRunnableList = (ArrayList<Runnable>)runnables.clone();
                    runnables.clear();
                    for (Runnable runnable : copiedRunnableList) {
                        runnable.run();
                    }
                    processing = false;
                }
            }
        }.runTaskTimerAsynchronously(instance, 0L, 5L);
        return schedulerTask;
    }

    public void scheduleRunnable(Runnable runnable) {
        if (!processing) {
            runnables.add(runnable);
        } else {
            instance.getServer().getScheduler().runTaskLater(instance, new Runnable() {
                @Override
                public void run() {
                    runnables.add(runnable);
                }
            }, 1L);
        }
    }

    public void reloadScheduler() {
        schedulerTask.cancel();
        runnables.clear();
        processing = false;
        startScheduler();
    }

    public BukkitTask getSchedulerTask() {
        return this.schedulerTask;
    }
    public void cancelSchedulerTask() {
        this.schedulerTask.cancel();
    }
}
