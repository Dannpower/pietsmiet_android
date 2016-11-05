package de.pscom.pietsmiet.backend;

import android.graphics.drawable.Drawable;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.RssUtil.loadRss;

public class PietcastPresenter extends MainPresenter {
    private static String pietcastUrl = "http://www.pietcast.de/pietcast/feed/podcast/";

    public PietcastPresenter() {
        super(PIETCAST);
        parsePietcast();
    }

    /**
     * Loads the latests Piecasts
     */
    private void parsePietcast() {
        Observable.defer(() -> Observable.just(loadRss(pietcastUrl)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(Observable::from)
                .onBackpressureBuffer()
                .subscribe(element -> {
                    Drawable thumb = DrawableFetcher.getDrawableFromRss(element);
                    post = new Post();
                    post.setDescription(element.getDescription());
                    post.setTitle(element.getTitle());
                    post.setDatetime(element.getPubDate());
                    post.setThumbnail(thumb);
                    post.setPostType(PIETCAST);
                    posts.add(post);
                }, Throwable::printStackTrace, this::finished);
    }

}
