package com.topjohnwu.magisk.components;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.topjohnwu.magisk.MagiskManager;
import com.topjohnwu.magisk.R;
import com.topjohnwu.magisk.asyncs.MarkDownWindow;
import com.topjohnwu.magisk.utils.Utils;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ShellUtils;

import java.util.ArrayList;
import java.util.List;

public class MagiskInstallDialog extends CustomAlertDialog {
    public MagiskInstallDialog(Activity activity) {
        super(activity);
        MagiskManager mm = Utils.getMagiskManager(activity);
        String filename = Utils.fmt("Magisk-v%s(%d).zip",
                mm.remoteMagiskVersionString, mm.remoteMagiskVersionCode);
        setTitle(mm.getString(R.string.repo_install_title, mm.getString(R.string.magisk)));
        setMessage(mm.getString(R.string.repo_install_msg, filename));
        setCancelable(true);
        setPositiveButton(R.string.install, (d, i) -> {
            List<String> options = new ArrayList<>();
            options.add(mm.getString(R.string.download_zip_only));
            options.add(mm.getString(R.string.patch_boot_file));
            if (Shell.rootAccess()) {
                options.add(mm.getString(R.string.direct_install));
            }
            String s = ShellUtils.fastCmd("grep_prop ro.build.ab_update");
            if (!s.isEmpty() && Boolean.parseBoolean(s)) {
                options.add(mm.getString(R.string.install_inactive_slot));
            }
            new InstallMethodDialog(activity, options, filename).show();
        });
        setNegativeButton(R.string.no_thanks, null);
        if (!TextUtils.isEmpty(mm.magiskNoteLink)) {
            setNeutralButton(R.string.release_notes, (d, i) -> {
                if (mm.magiskNoteLink.contains("forum.xda-developers")) {
                    // Open forum links in browser
                    Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse(mm.magiskNoteLink));
                    openLink.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mm.startActivity(openLink);
                } else {
                    new MarkDownWindow(activity, null, mm.magiskNoteLink).exec();
                }
            });
        }
    }
}
