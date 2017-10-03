package com.keybackup.fullscreenfragments;

/**
 * Created by christoph on 09.09.17.
 */


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.print.PrintHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.Container;
import com.android.secret.sharing.KeyPart;
import com.keybackup.R;
import com.secure.key.backup.KeyPartImp;

import java.math.BigInteger;

import ShamirKeypendant.AndroidBild;
import ShamirKeypendant.AndroidKeyValueParametersImplementation;
import ShamirKeypendant.Bild;
import ShamirKeypendant.CustomParameterFullpage;
import ShamirKeypendant.CustomParameterQRPage;
import ShamirKeypendant.KeyValueParameters;
import ShamirKeypendant.QRPage;
import ShamirKeypendant.RAWDataPendantLabelImplementation;
import ShamirKeypendant.SingleIDFullpage;

/**
 * Created by christoph on 21.08.17.
 */

public class KeyPendantDialog extends DialogFragment implements QRPage.PageGenerated{

    final String DBG = "XXXXXX ";
    int mNum;
    private View view;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static KeyPendantDialog newInstance(int num) {
        KeyPendantDialog f = new KeyPendantDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");
/*
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch ((mNum-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }*/

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // super.onCreateView(inflater,container,savedInstanceState);
        View v = inflater.inflate(R.layout.image_view_dialog, container, false);
        // ImageView img = (ImageView)view.findViewById(R.id.imageView);

        // ((TextView)tv).setText("Dialog #" + mNum + ": using style "
        //       + getNameForNum(mNum));

        // Watch for button clicks.
        //  Button button = (Button)v.findViewById(R.id.show);
        // button.setOnClickListener(new View.OnClickListener() {
        /*    public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                ((FragmentDialog)getActivity()).showDialog();
            }
        });
*/


        AndroidSecretSharing as = new AndroidSecretSharing(getContext());
        Container c  = as.getContainer();
        // KeyPart[] newkeys =  c.createPrivateKeyParts(getContext());
        KeyPart[] keys = as.getUserKeyParts();
        //
        BigInteger[] parts = new BigInteger[keys.length];
        int i = 0;
        for (KeyPart key :
                keys) {

            KeyPartImp kpi = (KeyPartImp)key;

            parts[i] = new BigInteger(kpi.getEncoded());
            i++;
        }
        KeyValueParameters kvp = AndroidKeyValueParametersImplementation.getInstance();
        kvp.put(RAWDataPendantLabelImplementation.OWNER,keys[0].getOwner());

        CustomParameterQRPage qrp = new CustomParameterQRPage(parts,300, QRPage.PNG,
                kvp);

        CustomParameterFullpage cpf = new CustomParameterFullpage(parts,300,QRPage.PNG,kvp);

        cpf.GeneratePageAndCallback(
                this
        );
     //   qrp.GeneratePageAndCallback(this);
        this.view = v;
        return v;
    }


    @Override
    public void PageReady(Bild bild) {
        AndroidBild bild1 = (AndroidBild)bild;


        PrintHelper ph = new PrintHelper(getContext());
        ph.printBitmap("pendantkeys", bild1.getBitmap());

    }

    @Override
    public void PagesReady(Bild[] bilder) {
        /* TODO implement */
        System.err.println("So viele Seiten wurden zurückgeliefert: "+bilder.length);
    }
}