package com.tci.consultoria.tcibitacora.Background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receivercall extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,IntentService.class));
    }
}
