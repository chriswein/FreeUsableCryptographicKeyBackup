package com.keybackup;

import com.android.secret.sharing.AndroidSecretSharing;
import com.keybackup.main.backup.BackupContract;
import com.keybackup.main.backup.BackupPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for the implementation of {@link BackupPresenter}
 */
public class OverviewBackupTest {
    private BackupPresenter mPresenter;

    @Mock
    private BackupContract.View mView;

    @Mock
    private AndroidSecretSharing mSecretSharing;
//
//    @Mock
//    private AndroidSecretBackup mBackup;

    @Before
    public void init() {
//        MockitoAnnotations.initMocks(this);
//
//        mPresenter = new KeyPartPresenter(mView, mSecretSharing);
    }

    @Test
    public void clickOpensBackup() {
//        mPresenter.restoreBackup(mBackup);
//
//        verify(mView).restoreBackup(mBackup);
    }
}
