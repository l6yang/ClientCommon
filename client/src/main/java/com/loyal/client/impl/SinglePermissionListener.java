package com.loyal.client.impl;

import androidx.annotation.IntRange;

public interface SinglePermissionListener {
    void onSinglePermission(@IntRange(from = 2, to = 1000) int reqCode, boolean successful);
}