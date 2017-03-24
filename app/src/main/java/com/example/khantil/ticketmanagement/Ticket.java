package com.example.khantil.ticketmanagement;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Khantil on 25-05-2016.
 */
public class Ticket implements Parcelable {
    long tid;   //ticket id
    String ticketTitle;
    int cid;    //Client Id
    int pid;       //Project Id
    String projectName;
    int issueId;    //issueId
    String issueName;
    String ticketDescription;
    String status;
    String contactName;
    String contactNumber;
    String contactEmail;
    String[] imagePaths;

    public Ticket(long tid, String ticketTitle, int cid, int pid, String projectName,int issueId,String issueName,String ticketDescription, String status,
                  String contactName,String contactNumber, String contactEmail, String imagePath1,String imagePath2,String imagePath3){
        this.tid = tid;
        this.ticketTitle = ticketTitle;
        this.cid = cid;
        this.pid = pid;
        this.projectName = projectName;
        this.issueId= issueId;
        this.issueName = issueName;
        this.ticketDescription = ticketDescription;
        this.status = status;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;
        imagePaths = new String[3];
        this.imagePaths[0] = imagePath1;
        this.imagePaths[1] = imagePath2;
        this.imagePaths[2] = imagePath3;

    }

    protected Ticket(Parcel in) {
        tid = in.readLong();
        ticketTitle = in.readString();
        cid = in.readInt();
        pid = in.readInt();
        projectName = in.readString();
        issueId = in.readInt();
        issueName = in.readString();
        ticketDescription = in.readString();
        status = in.readString();
        contactName = in.readString();
        contactNumber = in.readString();
        contactEmail = in.readString();
        imagePaths = in.createStringArray();
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(tid);
        dest.writeString(ticketTitle);
        dest.writeInt(cid);
        dest.writeInt(pid);
        dest.writeString(projectName);
        dest.writeInt(issueId);
        dest.writeString(issueName);
        dest.writeString(ticketDescription);
        dest.writeString(status);
        dest.writeString(contactName);
        dest.writeString(contactNumber);
        dest.writeString(contactEmail);
        dest.writeStringArray(imagePaths);
    }

    @Override
    public String toString() {
        return "\n Ticket: "+tid+" : "+ticketTitle+" Client id: "+cid+" , project id:"+pid ;
    }
}
