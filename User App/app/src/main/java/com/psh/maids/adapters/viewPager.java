package com.psh.maids.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.psh.maids.Activities.MainActivity;
import com.psh.maids.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

public class viewPager extends PagerAdapter {
    LayoutInflater inflater;
    Context context;
    //ArrayList<String> images;
    Bitmap selectedImage, bmp, image;
    byte[] byteArray;
    int[] images;

    public viewPager(MainActivity mainActivity, int[] img) {
        this.context = mainActivity;
        this.images = img;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final ImageView imageView;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.pager_item, container, false);
        imageView = itemView.findViewById(R.id.ima1);
        Picasso.with(context).load(images[position]).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                selectedImage = bitmap;
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                byteArray = bs.toByteArray();
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                image = getResizedBitmap(bmp, 300);
                imageView.setImageBitmap(image);
                container.addView(itemView);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
        //Picasso.with(context).load(images.get(position)).into(img);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        container.removeView((RelativeLayout) object);
    }

    @Override
    public float getPageWidth(int position) {
        return 1f;   //it is used for set page width of view pager
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}

