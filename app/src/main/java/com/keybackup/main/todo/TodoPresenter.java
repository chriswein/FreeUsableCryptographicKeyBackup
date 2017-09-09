package com.keybackup.main.todo;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.Backup;
import com.android.secret.sharing.Contact;
import com.android.secret.sharing.Container;
import com.android.secret.sharing.KeyPart;
import com.android.secret.sharing.QrCodeSizeException;
import com.keybackup.BaseActivity;
import com.keybackup.DialogFinishListener;
import com.keybackup.Logger;
import com.keybackup.Preferences;
import com.keybackup.R;
import com.keybackup.Utils;
import com.keybackup.fullscreenfragments.KeyPendantDialog;
import com.keybackup.stepperfragments.initkey.InitKeyDialog;
import com.keybackup.fullscreenfragments.FullscreenFragmentActivity;
import com.keybackup.main.todo.adapter.TodoAdapter;
import com.keybackup.main.todo.adapter.item.TodoContactItem;
import com.keybackup.main.todo.adapter.item.TodoProgressItem;
import com.keybackup.main.todo.adapter.item.TodoTextItem;

public class TodoPresenter implements TodoContract.UserActionsListener {
    private TodoContract.TodoView mView;
    private AndroidSecretSharing mSecretSharing;
    private Preferences mPreferences;
    private BaseActivity mActivity;

    private TodoAdapter mAdapter;

    public TodoPresenter(BaseActivity activity, TodoContract.TodoView view, AndroidSecretSharing secretSharing) {
        mActivity = activity;
        mView = view;
        mSecretSharing = secretSharing;

        mPreferences = Preferences.getPreferences(activity);
    }

    @Override
    public void setAdapter(RecyclerView view) {
        mAdapter = new TodoAdapter(mActivity);
        view.setAdapter(mAdapter);
    }

    @Override
    public void loadCards(final BaseActivity activity) {
        Resources resources = mActivity.getResources();

        mAdapter.clear();

        boolean keySetupFinished = true;

        addMassExportBackupCard(resources,null);

        if (!mSecretSharing.keyShared()) {
            final KeyPart[] keyParts = mSecretSharing.getUserKeyParts();
            final Contact[] selectedContacts = mSecretSharing.getContactsWithStatusSelected();

            boolean showKeyPartsCard = keyParts != null && keyParts.length > 0;
            boolean showSendKeyPartCards = selectedContacts != null && selectedContacts.length > 0;
            boolean containerGenerated = mPreferences.isContainerCreated();

            if (!containerGenerated && !showKeyPartsCard && !showSendKeyPartCards) {
                keySetupFinished = false;
                addKeyCard(resources);
            } else if (showKeyPartsCard || showSendKeyPartCards) {
                if (showKeyPartsCard) {
                    addKeyShareCard(resources, keyParts);

                    if (keyParts[0].getMinimumKeyParts(mActivity) <= keyParts.length) {
                        addWarningCard(resources);
                    }
                }

                if (showSendKeyPartCards) {
                    for (final Contact selectedContact : selectedContacts) {
                        addContactCard(selectedContact, keyParts);
                    }
                }
            }
        }

        Backup[] backups = mSecretSharing.getAvailableBackups();
        if (backups != null) {
            for (final Backup backup : backups) {
                addExportBackupCard(resources, backup);
            }
        }

        // add help card if key is split and no backup has been created yet
        if (keySetupFinished && !mPreferences.isCardHelpBackupRemoved()) {
            if (backups != null && backups.length > 0) {
                mPreferences.cardHelpBackupRemoved();
            } else {
                addBackupHelpCard(resources);
            }
        }
    }

    private void addKeyCard(Resources resources) {
        TodoTextItem item = new TodoTextItem(resources, R.string.card_setup_key_headline, R.string.card_setup_key_description);
        item.setButton(resources, R.string.card_setup_key_setup, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.startSplitKey("Card (Setup Button)");

                InitKeyDialog dialog = InitKeyDialog.newInstance(false);
                dialog.setListener(new DialogFinishListener() {
                    @Override
                    public void onFinished() {
                        loadCards(mActivity);
                    }
                });

                Utils.openFullscreenDialog(mActivity, dialog, InitKeyDialog.TAG);
            }
        });

        mAdapter.addItem(item);
    }

    private void addKeyShareCard(Resources resources, final KeyPart[] keyParts) {
        Container container = mSecretSharing.getContainer();
        int progress = container.getTotalRecoverParts() - keyParts.length;

        String description = String.format(resources.getString(R.string.card_share_key_description), keyParts.length);
        String status = String.format(resources.getString(R.string.card_share_key_progress_description), progress, container.getTotalRecoverParts());

        TodoProgressItem item = new TodoProgressItem();
        item.setHeadline(resources.getString(R.string.card_share_key_headline));
        item.setDescription(description);
        item.setProgressStatus(status);
        item.setMaxProgress(container.getTotalRecoverParts());
        item.setCurrentProgress(progress);
        item.setButton(resources, R.string.card_share_key_start, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.opensChooseContactsDialog("Card ('Share key parts' button)");
                Bundle bundle = FullscreenFragmentActivity.getContactListArguments(keyParts);
                Utils.startActivity(mActivity, FullscreenFragmentActivity.class, false, bundle, -1);
            }
        });

        mAdapter.addItem(item);
    }

    private void addContactCard(final Contact selectedContact, final KeyPart[] keyParts) {
        final TodoContactItem item = new TodoContactItem(mActivity, selectedContact, true);

        if (selectedContact.getSendMethod() != com.android.secret.sharing.Contact.SendMethod.QR) {
            item.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyPart keyPart = selectedContact.getKeyPart(mActivity);
                    keyPart.delete(mActivity);

                    selectedContact.setSendStatus(Contact.SendStatus.CONFIRMED);
                    selectedContact.removeKeyPart(null);
                    selectedContact.save(mActivity);

                    mAdapter.removeItem(item);
                }
            });
        }

        item.setShowButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedContact.getSendMethod()) {
                    case QR:
                        KeyPart keyPart = selectedContact.getKeyPart(mActivity);
                        Utils.startActivity(mActivity, FullscreenFragmentActivity.class, false, FullscreenFragmentActivity.getQrCodeArguments(keyPart, selectedContact), -1);
                        break;
                    case PRINT:
                        try {
                            mSecretSharing.printQrCode(mActivity, selectedContact);
                        } catch (QrCodeSizeException e) {
                            // Secret parts should never be too large
                            e.printStackTrace();
                        }
                        break;
                    case EMAIL:
                        try {
                            mSecretSharing.sendEmail(mActivity, selectedContact);
                        } catch (QrCodeSizeException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        item.setMoreButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.opensChooseContactsDialog("Contact card");
                Bundle bundle = FullscreenFragmentActivity.getContactListArguments(keyParts);
                Utils.startActivity(mActivity, FullscreenFragmentActivity.class, false, bundle, -1);
            }
        });

        mAdapter.addItem(item);
    }

    /**
     * {@author christoph}
     */
    private void addMassExportBackupCard(Resources resources, final Backup backup) {
        final TodoTextItem item = new TodoTextItem();
        item.setHeadline("Backup all Parts as Keypendants");
        // item.setDescription(String.format(mActivity.getString(R.string.card_backup_print_description), backup.getName()));

       /* item.setRemovable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup.removeBackupData(mActivity);
                mAdapter.removeItem(item);
            }
        });*/

        item.setButton(resources, R.string.card_share_key_start, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *
                 */
                KeyPendantDialog newFragment = KeyPendantDialog.newInstance(5);
                Utils.openFullscreenDialog(mActivity ,newFragment,"TAG");
            }
        });

        /* TODO re-add option to send email

        item.setSecondButton(resources, R.string.card_backup_email_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSecretSharing.sendEmail(mActivity, backup);
                } catch (QrCodeSizeException e) {
                    e.printStackTrace();

                    mView.showMessage(mActivity.getString(R.string.create_backup_qr_code_error));

                    backup.removeBackupData(mActivity);
                    mAdapter.removeItem(item);
                }
            }
        });*/

     /*   item.setSecondButton(resources, R.string.card_backup_cloud_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.exportBackupFromCard(true);
                mSecretSharing.saveToCloud(backup);
            }
        });
*/
        mAdapter.addItem(item);
    }

    private void addExportBackupCard(Resources resources, final Backup backup) {
        final TodoTextItem item = new TodoTextItem();
        item.setHeadline(backup.getName());
        item.setDescription(String.format(mActivity.getString(R.string.card_backup_print_description), backup.getName()));

        item.setRemovable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup.removeBackupData(mActivity);
                mAdapter.removeItem(item);
            }
        });

        item.setButton(resources, R.string.card_backup_print_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.exportBackupFromCard(false);
                try {
                    mSecretSharing.printQrCode(mActivity, backup);
                } catch (QrCodeSizeException e) {
                    e.printStackTrace();

                    mView.showMessage(mActivity.getString(R.string.create_backup_qr_code_error));

                    backup.removeBackupData(mActivity);
                    mAdapter.removeItem(item);
                }
            }
        });

        /* TODO re-add option to send email

        item.setSecondButton(resources, R.string.card_backup_email_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSecretSharing.sendEmail(mActivity, backup);
                } catch (QrCodeSizeException e) {
                    e.printStackTrace();

                    mView.showMessage(mActivity.getString(R.string.create_backup_qr_code_error));

                    backup.removeBackupData(mActivity);
                    mAdapter.removeItem(item);
                }
            }
        });*/

        item.setSecondButton(resources, R.string.card_backup_cloud_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.exportBackupFromCard(true);
                mSecretSharing.saveToCloud(backup);
            }
        });

        mAdapter.addItem(item);
    }

    private void addBackupHelpCard(Resources resources) {
        final TodoTextItem item = new TodoTextItem(resources, R.string.card_help_backup_headline, R.string.card_help_backup_description);
        item.setRemovable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.cardHelpBackupRemoved();
                mAdapter.removeItem(item);
            }
        });

        mAdapter.addItem(item);
    }

    private void addWarningCard(Resources resources) {
        final TodoTextItem item = new TodoTextItem(resources, R.string.card_warning_headline, R.string.card_warning_description);
        item.setHeadlineColor(Color.RED);
        mAdapter.addItem(item);
    }
}
