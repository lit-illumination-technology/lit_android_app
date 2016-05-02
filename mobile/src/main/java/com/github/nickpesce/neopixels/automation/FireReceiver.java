package com.github.nickpesce.neopixels.automation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.nickpesce.neopixels.CommandSender;

/**
 * Created by nick on 4/3/16.
 */
public final class FireReceiver extends BroadcastReceiver{

    public static final String BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE";
    public static final String BUNDLE_COMMAND = "command";
    public static final String BUNDLE_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!intent.getAction().equals("com.twofortyfouram.locale.intent.action.FIRE_SETTING"))
                return;

        final Bundle bundle = intent.getBundleExtra(BUNDLE);
        final String command = bundle.getString(BUNDLE_COMMAND);

        CommandSender sender = new CommandSender(context);
        sender.startEffect(command);
    }
}
