package test.upwork.timer.player;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.upwork.timer.util.FileFinder;


/**
 * Created by dakishin@gmail.com
 */

public class MusicBrowserService extends MediaBrowserServiceCompat {
    public static final String MEDIA_ID_ROOT = "__ROOT__";

    private MediaSessionCompat mSession;


    @Override
    public void onCreate() {
        super.onCreate();
        mSession = new MediaSessionCompat(this, "MusicService");
        mSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
            }

            @Override
            public void onStop() {
                super.onStop();
            }
        });
        setSessionToken(mSession.getSessionToken());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new MediaBrowserServiceCompat.BrowserRoot(MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        ArrayList<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
        File root = new File("/");
        FileFinder fileSearcher = new FileFinder();
        fileSearcher.search(root);
        for (String fileName : fileSearcher.getFilePaths()) {
            MediaBrowserCompat.MediaItem item = new MediaBrowserCompat.MediaItem(
                new MediaDescriptionCompat.Builder()
                    .setMediaId(fileName)
                    .setMediaUri(Uri.parse(fileName))
                    .build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
            );
            items.add(item);
        }
        result.sendResult(items);
    }


}
