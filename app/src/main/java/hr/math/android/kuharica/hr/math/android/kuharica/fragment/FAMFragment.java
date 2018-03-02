package hr.math.android.kuharica.hr.math.android.kuharica.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import hr.math.android.kuharica.R;

/**
 * Created by mabel on 27-Feb-18.
 */

public class FAMFragment extends Fragment {
    private FloatingActionMenu floatingActionMenu;
    private com.github.clans.fab.FloatingActionButton famKategorija;
    private com.github.clans.fab.FloatingActionButton famRecept;
    public boolean prikaziObaKruga = true;

    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fam_menus_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        floatingActionMenu = (FloatingActionMenu)view.findViewById(R.id.menu_fam);
        floatingActionMenu.hideMenuButton(false);

        famKategorija = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fabKategorija);
        famRecept = (com.github.clans.fab.FloatingActionButton)view.findViewById(R.id.fabRecept);

        famKategorija.setLabelText("Nova kategorija");
        famRecept.setLabelText("Novi recept");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int delay = 400;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                floatingActionMenu.showMenuButton(true);
            }
        }, delay);
    }
}
