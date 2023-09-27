package com.psh.maids.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.psh.maids.fragments.cart;
import com.psh.maids.R;
import com.psh.maids.models.Servant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class servantAdapter extends ArrayAdapter<Servant> {
    Servant currentServant;
    private int mType;
    private ArrayList<Servant> mAddedToCart;
    private Context context;
    private CircleImageView servantImage;

    public servantAdapter(@NonNull Context context, int resource, ArrayList<Servant> list, int type) {
        super(context, resource, list);
        mType = type;
        this.context = context;
        this.mAddedToCart = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (mType == 1) {
            if (listItemView == null)
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.servant_list_item, parent, false);
            currentServant = getItem(position);
            TextView name = listItemView.findViewById(R.id.name);
            TextView age = listItemView.findViewById(R.id.age);
            TextView avail = listItemView.findViewById(R.id.availability);
            TextView rate = listItemView.findViewById(R.id.rating);
            TextView cost = listItemView.findViewById(R.id.cost);
            TextView distance = listItemView.findViewById(R.id.distance);
            TextView eta = listItemView.findViewById(R.id.eta);
            servantImage = listItemView.findViewById(R.id.image);
            String imageUrl = currentServant.getImageUrl();
            Picasso.with(context).load(imageUrl).resize(75, 75).placeholder(R.drawable.user).into(servantImage);
            name.setText(currentServant.getName());
            age.setText("Age: " + currentServant.getAge() + "/" + currentServant.getGender());
            rate.setText("" + currentServant.getRating());
            cost.setText("Rs. " + currentServant.getCost() + "/hr");
//            avail.setText("Available S,M,T,W,T,F,S 8-12 12-4 4-8");
//            distance.setText("Distance: 25km");
//            eta.setText("ETA: 25min");

            return listItemView;
        } else if (mType == 2) {
            if (listItemView == null)
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.cart_list_item, parent, false);
            final Servant currentServant = getItem(position);
            servantImage = listItemView.findViewById(R.id.image);
            final TextView category = listItemView.findViewById(R.id.category);
            TextView name = listItemView.findViewById(R.id.name);
            TextView ageSex = listItemView.findViewById(R.id.age_sex);
            TextView avail = listItemView.findViewById(R.id.availability);
            TextView cost = listItemView.findViewById(R.id.cost);
            Button delete = listItemView.findViewById(R.id.delete_from_cart);
            delete.setTag(position);
            final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("deleting..");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int) v.getTag();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE);
                    String firebaseId = sharedPreferences.getString("firebaseId", null);
                    String path = "customerPanel/cart/" + firebaseId;
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(path).document(currentServant.getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    dialog.dismiss();
                                }
                            });
                    mAddedToCart.remove(id);
                    notifyDataSetChanged();
                    cart.listStateChanged();
                }
            });
            //category.setText("Added " + currentServant.getCategory());
            Picasso.with(context).load(currentServant.getImageUrl()).resize(65, 65).placeholder(R.drawable.user).into(servantImage);
            avail.setText("Available");
            name.setText(currentServant.getName());
            ageSex.setText("Age: " + currentServant.getAge() + "/" + currentServant.getGender());
            cost.setText("" + currentServant.getCost());
            return listItemView;
        }
        return listItemView;
    }
}
