package com.app.tanyahukum.presenter;

import android.content.Context;
import android.util.Log;

import com.app.tanyahukum.model.Consultations;
import com.app.tanyahukum.util.Config;
import com.app.tanyahukum.view.ListConsultationInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by emerio on 4/9/17.
 */

public class ListConsultationPresenter  implements ListConsultationInterface.Presenter{
    FirebaseDatabase firebase;
    ListConsultationInterface.View view;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    Context context;
    @Inject
    public ListConsultationPresenter(FirebaseDatabase firebase, ListConsultationInterface.View view, Context context) {
        this.firebase = firebase;
        this.view = view;
        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = this.firebase.getReference("questions");
        this.context = context;

    }

    @Override
    public void getQuestionsByUser(String userId) {
        view.showQuestionsProgress(true);
        Query userQuery;
        if(Config.USER_TYPE.equalsIgnoreCase("CLIENT")){
            userQuery = dbRef.orderByChild("clientId").equalTo(userId);
        }else{
            userQuery = dbRef.orderByChild("consultantId").equalTo(userId);
        }
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("data consult : ", dataSnapshot.toString());
                List<Consultations> consultationsList=new ArrayList<>();
                Consultations consultations=null;
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    consultations = singleSnapshot.getValue(Consultations.class);
                    if(consultations.getPublish().equals("true")) {
                        consultationsList.add(consultations);
                    }
                }
                if (consultationsList.size()>0) {
                    sortingConsultationList(consultationsList);
                    view.showQuestionsProgress(false);
                    view.showQuestions(consultationsList);
                }else {
                    view.showQuestionsProgress(false);
                    view.showEmptyMessage();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void deleteConsultationById(String consultationId) {
        view.showQuestionsProgress(true);
        dbRef.child(consultationId).child("publish").setValue("false");
        view.showQuestionsProgress(false);
    }

    public void sortingConsultationList(List<Consultations> consultationsList){
        Collections.sort(consultationsList, new Comparator<Consultations>() {
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
            @Override
            public int compare(Consultations o1, Consultations o2) {
                try {
                    return dateFormat.parse(o2.getConsultationsDate()).compareTo(dateFormat.parse(o1.getConsultationsDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }
}
