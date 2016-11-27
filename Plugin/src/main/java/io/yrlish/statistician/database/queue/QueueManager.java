/*
 * MIT License
 *
 * Copyright (c) 2016 Dennis Alexandersson (Yrlish)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.yrlish.statistician.database.queue;

import io.yrlish.statistician.Statistician;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class QueueManager {
    private static boolean started = false;

    private static Task task;

    /**
     * Initiates the queue consumer.
     */
    public static void start() {
        if (started) {
            Statistician.getLogger().error("QueueManager has already started. Cannot start it again.");
            return;
        }

        Scheduler scheduler = Sponge.getScheduler();
        task = scheduler.createTaskBuilder()
                .async()
                .name("StatisticianQueueConsumer")
                .delay(30, TimeUnit.SECONDS)
                .interval(10, TimeUnit.SECONDS)
                .execute(new QueueConsumer())
                .submit(Statistician.getInstance());

        started = true;
    }

    /**
     * Cancels the queue consumer task.
     */
    public static void stop() {
        if (!started) {
            Statistician.getLogger().error("QueueManager is not running. Cannot stop it.");
            return;
        }

        task.cancel();

        started = false;
    }

}
