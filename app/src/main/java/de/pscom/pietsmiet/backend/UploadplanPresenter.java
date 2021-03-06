package de.pscom.pietsmiet.backend;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PsLog;
import rx.Observable;
import rx.schedulers.Schedulers;

import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;

public class UploadplanPresenter extends MainPresenter {
    private static final String KEY_DATE = "date";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_DESCRIPTION = "desc";

    static final int MAX_COUNT = 2;

    public UploadplanPresenter(MainActivity view) {
        super(view, UPLOADPLAN);
        parseUploadplanFromDb("uploadplan");
        parseUploadplanFromDb("news");
    }

    private void parseUploadplanFromDb(String scope) {
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child(scope);

        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postBuilder = new Post.PostBuilder(UPLOADPLAN);
                Observable.just(dataSnapshot.getChildren())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .onBackpressureBuffer()
                        .subscribe(snapshots -> {
                            for (DataSnapshot snapshot :
                                    snapshots) {
                                String value = (String) snapshot.getValue();
                                switch (snapshot.getRef().getKey()) {
                                    case KEY_DATE:
                                        postBuilder.date(new Date(value));
                                        break;
                                    case KEY_TITLE:
                                        postBuilder.title(value);
                                        break;
                                    case KEY_LINK:
                                        postBuilder.url(value);
                                        break;
                                    case KEY_DESCRIPTION:
                                        postBuilder.description(value);
                                    default:
                                        break;
                                }
                            }
                            posts.add(postBuilder.build());
                            finished();
                            PsLog.v("added " + scope + " from firebase db");

                        }, (throwable) -> {
                            throwable.printStackTrace();
                            view.showError("Typ" + scope + " konnte nicht geladen werden");
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null)
                    PsLog.e("Database loading failed because: " + databaseError.toString());
                view.showError("Typ" + scope + " konnte nicht geladen werden");
            }
        });
    }


}
