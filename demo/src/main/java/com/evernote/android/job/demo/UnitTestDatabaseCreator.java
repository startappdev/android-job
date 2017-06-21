package com.evernote.android.job.demo;

import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

/**
 * @author rwondratschek
 */
public final class UnitTestDatabaseCreator {

    private static boolean random() {
        return Math.random() > 0.5;
    }

    public void createV1() {
        createJobs(new DummyJobCreatorV1());
    }

    public void createV2() {
        createJobs(new DummyJobCreatorV1()); // same as v1, only transient column is new
    }

    public void createV3() {
        createJobs(new DummyJobCreatorV3());
    }

    public void createV4() {
        createJobs(new DummyJobCreatorV3()); // same as v3, only last run column is new
    }

    private void createJobs(DummyJobCreator creator) {
        creator.createOneOff();
        creator.createExact();
        creator.createPeriodic();
    }

    private interface DummyJobCreator {
        void createOneOff();

        void createExact();

        void createPeriodic();
    }

    private static class DummyJobCreatorV1 implements DummyJobCreator {
        @Override
        public void createOneOff() {
            for (int i = 0; i < 10; i++) {
                JobRequest.Builder builder = new JobRequest.Builder("tag")
                        .setExecutionWindow(300_000, 400_000)
                        .setBackoffCriteria(5_000L, random() ? JobRequest.BackoffPolicy.EXPONENTIAL : JobRequest.BackoffPolicy.LINEAR)
                        .setRequiresCharging(random())
                        .setRequiresDeviceIdle(random())
                        .setRequiredNetworkType(random() ? JobRequest.NetworkType.ANY : JobRequest.NetworkType.CONNECTED)
                        .setRequirementsEnforced(random())
                        .setPersisted(random());

                if (random()) {
                    String extras = "Hello world";
                    builder.setData(extras);
                }

                builder.build().schedule();
            }

        }

        @Override
        public void createExact() {
            for (int i = 0; i < 10; i++) {
                JobRequest.Builder builder = new JobRequest.Builder("tag")
                        .setExact(400_000)
                        .setBackoffCriteria(5_000L, random() ? JobRequest.BackoffPolicy.EXPONENTIAL : JobRequest.BackoffPolicy.LINEAR)
                        .setPersisted(random());

                if (random()) {
                    String extras = "Hello world";
                    builder.setData(extras);
                }

                builder.build().schedule();
            }

        }

        @Override
        public void createPeriodic() {
            for (int i = 0; i < 10; i++) {
                JobRequest.Builder builder = new JobRequest.Builder("tag")
                        .setPeriodic(TimeUnit.MINUTES.toMillis(1))
                        .setRequiresCharging(random())
                        .setRequiresDeviceIdle(random())
                        .setRequiredNetworkType(random() ? JobRequest.NetworkType.ANY : JobRequest.NetworkType.CONNECTED)
                        .setRequirementsEnforced(random())
                        .setPersisted(random());

                if (random()) {
                    String extras = "Hello world";
                    builder.setData(extras);
                }

                builder.build().schedule();
            }
        }
    }

    private static final class DummyJobCreatorV3 extends DummyJobCreatorV1 {
        @Override
        public void createPeriodic() {
            for (int i = 0; i < 10; i++) {
                JobRequest.Builder builder = new JobRequest.Builder("tag")
                        .setRequiresCharging(random())
                        .setRequiresDeviceIdle(random())
                        .setRequiredNetworkType(random() ? JobRequest.NetworkType.ANY : JobRequest.NetworkType.CONNECTED)
                        .setRequirementsEnforced(random())
                        .setPersisted(random());

                if (random()) {
                    String extras = "Hello world";
                    builder.setData(extras);
                }
                if (random()) {
                    builder.setPeriodic(JobRequest.MIN_INTERVAL);
                } else {
                    builder.setPeriodic(JobRequest.MIN_INTERVAL, JobRequest.MIN_FLEX);
                }

                builder.build().schedule();
            }

        }
    }
}
