package ru.androidacademy.spb.imguruploader.work;

import android.support.annotation.NonNull;

import androidx.work.Worker;

/**
 * @author Artur Vasilov
 */
public class UploadWork extends Worker {

    @NonNull
    @Override
    public WorkerResult doWork() {
        // TODO : upload photo (we are on working thread)
        return WorkerResult.SUCCESS;
    }
}
