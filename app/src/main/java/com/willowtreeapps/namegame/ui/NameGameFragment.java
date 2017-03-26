package com.willowtreeapps.namegame.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.GameMode;
import com.willowtreeapps.namegame.core.ListRandomizer;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.core.NameGameManager;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class NameGameFragment extends Fragment {

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();
    private static final Interpolator DECELERATE = new DecelerateInterpolator();
    private static final Interpolator ACCELERATE = new AccelerateInterpolator();
    @Inject
    ListRandomizer randomizer;

    @Inject
    Picasso picasso;

    @Inject
    NameGameManager gameManager;

    private TextView title;
    private ViewGroup container;
    private List<Integer> fadedTags = new ArrayList<>(5);
    private List<ImageView> faces = new ArrayList<>(5);
    private MediaPlayer mp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.name_game_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        title = (TextView) view.findViewById(R.id.title);
        container = (ViewGroup) view.findViewById(R.id.face_container);

        //Hide the views until data loads
        title.setAlpha(0);
        //clear faces before adding
        clearImages();
        int n = container.getChildCount();
        for (int i = 0; i < n; i++) {
            ImageView face = (ImageView) container.getChildAt(i);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //use the tags set in the xml to retrieve the list object
                    onPersonSelected(v,gameManager.getPeople().get(Integer.parseInt((String)v.getTag())));
                }
            });
            faces.add(face);
        }
        generateViewsFromGame(gameManager);
    }
    private void generateViewsFromGame(NameGameManager gameManager){

        if(gameManager.isGameOver()==false){
            final List<Person> people = gameManager.getPeople();
            final Person thePerson = gameManager.getThePerson();


            String strTitle = getString(R.string.question).replace("%s",
                    thePerson.getFirstName()
                            .concat(", ")
                            .concat(thePerson.getLastName()));
            title.setText(strTitle);

            if(gameManager.getGameMode().equals(GameMode.hint)){
                resetFaceScale(1);
            }else{
                resetFaceScale(0);
            }
            setImages(faces,people);
        }else{
            Intent stats = new Intent(getActivity(),NameGameResultsActivity.class);
            startActivity(stats);
            getActivity().finish();
        }

    }
    private void resetFaceScale(int scale){
        for(ImageView face: faces){
            face.setScaleX(scale);
            face.setScaleY(scale);
        }
    }
    /**
     * A method for setting the images from people into the imageviews
     */
    private void setImages(List<ImageView> faces, final List<Person> people) {
        int imageSize = (int) Ui.convertDpToPixel(100, getContext());
        int n = faces.size();

        for (int i = 0; i < n; i++) {
            final ImageView face = faces.get(i);

            //dispatch to its own thread? Wait for counter to be what i expect
            picasso.load("http:"+people.get(i).getHeadshot().getUrl())
                    .placeholder(R.drawable.ic_face_white_48dp)
                    .resize(imageSize, imageSize)
                    .transform(new CircleBorderTransform())
                    .into(face,new  com.squareup.picasso.Callback(){
                        public void onSuccess(){
                                Animation fadein = new AlphaAnimation(0, 1);
                                fadein.setInterpolator(ACCELERATE);
                                fadein.setDuration(2000);
                                face.startAnimation(fadein);
                        }
                        public void onError(){
                            System.out.println("failed to load image "+face.toString());
                        }
                    });
        }
        animateFacesIn();
        if(gameManager.getGameMode().equals(GameMode.hint)){
            animateFacesOut();
        }
        gameManager.startTimer();
    }


    /**
     * A method to animate the faces into view
     */
    private void animateFaceIn(final ImageView person){
        person.animate().scaleX(1).scaleY(1).setStartDelay(1500).setInterpolator(OVERSHOOT).start();
    }
    private void animateFaceOut(final ImageView person){
        person.animate().scaleX(0).scaleY(0).setStartDelay(0).setInterpolator(OVERSHOOT)
               .start();
    }
    private void gradualAnimateFaceOut(final ImageView person, int startDelayInterval){
        person.animate().scaleX(0).scaleY(0).setStartDelay(3000 *startDelayInterval)
                .setInterpolator(DECELERATE)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        person.setVisibility(View.INVISIBLE);
                        person.setScaleX(1);
                        person.setScaleY(1);
                    }
                })
                .start();
    }
    private void animateFacesIn() {
        title.animate().alpha(1).start();
        for (int i = 0; i < faces.size(); i++) {
            ImageView face = faces.get(i);
            animateFaceIn(face);
//            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT)
//                    .start();
        }
    }
    private Integer getThePersonTag(){
         return gameManager.getPeople().indexOf(gameManager.getThePerson());
    }
    private void animateFacesOut(){
        fadedTags.clear();
       for(int i=0;i<faces.size();i++) {
           final ImageView face = faces.get(i);
           Integer pos = (Integer.parseInt((String)face.getTag()));

           if(pos!=getThePersonTag()){
               fadedTags.add(pos);
               gradualAnimateFaceOut(face,i+1);
           }
       }
    }

    /**
     * A method to handle when a person is selected
     *
     * @param view   The view that was selected
     * @param person The person that was selected
     */
    private void onPersonSelected(@NonNull View view, @NonNull Person person) {
        stopPlaying();

        if(gameManager.gotCorrectAnswer(person)==true){
            //correct person was selected
            mp = MediaPlayer.create(getContext(),R.raw.correct);
            mp.start();
          //  System.out.println("The right person was "+thePerson.getFirstName()+", "+thePerson.getLastName());

            if(gameManager.getGameMode().equals(GameMode.hint)){
                for(ImageView face: faces){
                    face.setVisibility(View.VISIBLE);
                }
            }
            generateViewsFromGame(gameManager);

        }else{
            //wrong person was selected
            mp = MediaPlayer.create(getContext(),R.raw.incorrect);
            mp.start();
            animateFaceOut((ImageView)view);
            System.out.println(person.getFirstName()+", "+person.getLastName()+" was selected");
        }
    }

    private void stopPlaying(){
        if(mp!=null){
            mp.stop();
            mp.release();
            mp=null;
        }
    }
    private void clearImages(){
        faces.clear();
    }
    private void clearTitle(){
        if(null!=title){
            title.setText("");
        }
    }
}
