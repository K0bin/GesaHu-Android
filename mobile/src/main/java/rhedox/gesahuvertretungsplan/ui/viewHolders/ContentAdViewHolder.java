package rhedox.gesahuvertretungsplan.ui.viewHolders;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rhedox.gesahuvertretungsplan.R;

/**
 * Created by Robin on 21.11.2015.
 */


public class ContentAdViewHolder extends NativeAdViewHolder{

    @Bind(R.id.icon) ImageView icon;
    @Bind(R.id.title) AppCompatTextView title;
    @Bind(R.id.description) AppCompatTextView description;
    @Bind(R.id.callToAction) AppCompatButton callToAction;
    //@Bind(R.id.image) ImageView image;
    @Bind(R.id.advertiser) AppCompatTextView advertiser;

    public ContentAdViewHolder(ViewGroup view) {
        super(view);

        NativeContentAdView contentAddView = (NativeContentAdView) view;
        ButterKnife.bind(this, contentAddView);

        contentAddView.setCallToActionView(callToAction);
        contentAddView.setBodyView(description);
        contentAddView.setHeadlineView(title);
        contentAddView.setAdvertiserView(icon);
        contentAddView.setAdvertiserView(advertiser);

        setAdView(contentAddView);
    }

    @Override
    public void setAd(NativeAd ad) {
        NativeContentAd contentAdd = (NativeContentAd)ad;

        title.setText(contentAdd.getHeadline());
        icon.setImageDrawable(contentAdd.getLogo().getDrawable());
        advertiser.setText(contentAdd.getAdvertiser());
        callToAction.setText(contentAdd.getCallToAction());
        description.setText(contentAdd.getBody());

        //if(installAd.getImages() != null && installAd.getImages().size() > 0)
        //    image.setImageDrawable(installAd.getImages().get(0).getDrawable());

        super.setAd(ad);
    }

    public void destroy() {
        ButterKnife.unbind(this);
    }
}
