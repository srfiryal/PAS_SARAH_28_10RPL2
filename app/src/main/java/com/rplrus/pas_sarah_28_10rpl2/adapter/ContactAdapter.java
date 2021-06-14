package com.rplrus.pas_sarah_28_10rpl2.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rplrus.pas_sarah_28_10rpl2.R;
import com.rplrus.pas_sarah_28_10rpl2.activity.EditActivity;
import com.rplrus.pas_sarah_28_10rpl2.activity.MainActivity;
import com.rplrus.pas_sarah_28_10rpl2.model.ContactModel;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private ArrayList<ContactModel> dataList;
    private OnItemClickListener mListener;
    private MainActivity mContext;
    private int position;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ContactAdapter(MainActivity mContext, ArrayList<ContactModel> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rv_list, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_title.setText(dataList.get(position).getName());
        holder.tv_desc.setText(dataList.get(position).getGender() + ", " + dataList.get(position).getAge());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView tv_title, tv_desc;
        private ImageView img_list;
        private RelativeLayout relativeLayout;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_list);
            tv_desc = itemView.findViewById(R.id.tv_desc_list);
            img_list = itemView.findViewById(R.id.img_list);
            relativeLayout = itemView.findViewById(R.id.rv_layout_list);
            relativeLayout.setOnCreateContextMenuListener(this);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            position = getAdapterPosition();
            delete.setOnMenuItemClickListener(onEditMenu);
        }
    }

    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case 1:
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference().child("contacts");

                    ProgressDialog progressDialog = new ProgressDialog(mContext);
                    progressDialog.setMessage("Deleting your data...");
                    progressDialog.show();

                    reference.orderByChild("phone").equalTo(dataList.get(position).getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().removeValue();
                            }
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "Data has been deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            mContext.setResult(RESULT_OK, intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });
                    break;
            }
            return true;
        }
    };
}
