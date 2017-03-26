package com.willowtreeapps.namegame.core;

import android.support.annotation.NonNull;

import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.network.api.model.Profiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import javax.inject.Singleton;

@Singleton
public class NameGameManager {
    private List<Person> people;
    private Person thePerson;
    private Profiles profiles;
    private PriorityQueue<Long> timeSpentPerRound = new PriorityQueue<>(10,new Comparator<Long>(){
        public int compare(Long l1, Long l2){
            if(l1>l2){
                return -1;
            }else if(l2>l1){
                return 1;
            }
            return 0;
        }
    });
    private Long startTimer;

    private boolean isGameOver=false;
    int numCorrect=0;
    int numWrong=0;

    //the number of people objects to make
    int numPeopleToGenerate=5;
    //stop the game after we get 10 right
    int playUntil=10;
    private GameMode state;

    ProfilesRepository repo;
    ListRandomizer listRandomizer;

    public NameGameManager(){
        this.numPeopleToGenerate=5;
        people = new ArrayList<>(numPeopleToGenerate);
        state = GameMode.normal;
        createProfiles();
    }

    public NameGameManager(@NonNull ProfilesRepository repo, @NonNull ListRandomizer randomizer){
        this.repo=repo;
        this.listRandomizer=randomizer;
        this.numPeopleToGenerate=5;
        people = new ArrayList<>(numPeopleToGenerate);
        state = GameMode.normal;
        createProfiles();
    }
    public void startTimer(){
        this.startTimer=System.currentTimeMillis();
    }
    public void stopTimer(){
        timeSpentPerRound.offer(new Long(System.currentTimeMillis()-startTimer)/1000);
    }
    public void startNewGame(){
        this.isGameOver=false;
        numCorrect=0;
        people.clear();
        generateNewPeople(numPeopleToGenerate);
        setThePerson();
    }
    public boolean gotCorrectAnswer(Person selected){
        if(selected.equals(thePerson)){
            numCorrect++;
            generateNewPeople(numPeopleToGenerate);
            setThePerson();
            stopTimer();
            if(numCorrect>=playUntil){
                isGameOver=true;
            }
            return true;
        }else{
            numWrong++;
            return false;
        }
    }
    public void setGameMode(GameMode state){
        this.state=state;

        switch(state){
            case normal:
                this.numPeopleToGenerate=5;
                break;
            case hint:
                this.numPeopleToGenerate=5;
                break;
            case reverse:
                break;
            case matt:
                break;
            default:

        }
    }
    public void generateNewPeople(int nPeople){
        people = listRandomizer.pickN(profiles.getPeople(),nPeople);

    }
    public void setThePerson(){
        thePerson = listRandomizer.pickOne(people);
    }

    private void createProfiles(){
        repo.register(new ProfilesRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull Profiles people) {
                System.out.println("test from onLoadFinished " + people.toString());
                setProfile(people);
                generateNewPeople(numPeopleToGenerate);
                setThePerson();
                repo.unregister(this);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                error.printStackTrace();
            }
        });
    }
    /**
     * Filter out 'bad' persons in the profile. A 'bad' person is one who has a null headshot or
     * contains a 'featured-image-TEST'
     * @param profile
     */
    private void setProfile(Profiles profile){
        this.profiles=profile;
        ArrayList<Person> people = new ArrayList<>();
        for(int i=0;i<profiles.getPeople().size();i++){
            if(profiles.getPeople().get(i).getHeadshot().getUrl()==null ||
                    profiles.getPeople().get(i).getHeadshot().getUrl().equals("")){
                continue;
            }else if(profiles.getPeople().get(i).getHeadshot().getUrl().contains("featured-image-TEST")){
                continue;
            }
            people.add(profiles.getPeople().get(i));
        }
        this.profiles.setPeople(people);
    }
    public void setNumPeopleToGenerate(int numPeopleToGenerate){
        this.numPeopleToGenerate=numPeopleToGenerate;
    }
    public Long getLongestRound(){
        return this.timeSpentPerRound.peek();
    }
    public Long getTotalTime(){
        Iterator<Long> nextTime = timeSpentPerRound.iterator();
        int size = timeSpentPerRound.size();
        Long runningTotal = 0L;
        while(nextTime.hasNext()){
            Long time = nextTime.next();
            runningTotal+=time;
        }
        return runningTotal;
    }
    public Long getAverageTime(){
        Iterator<Long> nextTime = timeSpentPerRound.iterator();
        int size = timeSpentPerRound.size();
        Long runningTotal = 0L;
        while(nextTime.hasNext()){
            Long time = nextTime.next();
            runningTotal+=time;
        }
        return runningTotal/size;
    }
    public int getNumWrong(){
        return this.numWrong;
    }
    public int getScore(){
        return (numCorrect+numWrong)/(numWrong+1);
    }
    public boolean isGameOver(){
        return this.isGameOver;
    }
    public Profiles getProfile(){
        return this.profiles;
    }
    public List<Person> getPeople(){
        return this.people;
    }
    public Person getThePerson(){
        return this.thePerson;
    }
    public GameMode getGameMode(){
        return this.state;
    }
}
