package android.bignerdranch.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {

    private static final String TAG = "NerdLauncherFragment";

    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        super.onCreateView(inflater, container, saveInstanceState);
        View view = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);

        mRecyclerView = view.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return view;
    }
    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ResolveInfo mResolveInfo;
        private TextView mNameTextView;

        public ActivityHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView;
        }

        public void bindActivity(ResolveInfo resolveInfo) {                                     //Я так понял выводит на экран текстовую метку переданного resolveInfo
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();
            mNameTextView.setText(appName);
            mNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;                              //ActivityInfo - Сведения, которые можно получить о конкретном приложении activity или receiver. Это соответствует собранной информации от AndroidManifest.xml-код <activity > и теги < receiver>.

            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);     //(смотри ст 471) мы получаем имя пакета и имя класса из метаданных и используем их для создания явной активности методом Intent
            /*applicationInfo - Информация, собранная из тега <application>
            *packageName - Название этого пакета. Из тега <manifest> " name" атрибут
            * */

            startActivity(intent);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private final List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }

    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        /*addCategory - Добавить новую категорию к цели. Категории предоставляют дополнительную
        информацию о действии, которое выполняет намерение. При разрешении намерения будут использоваться
        только те действия, которые обеспечивают все запрошенные категории.*/

        PackageManager pm = getActivity().getPackageManager();
                        //PackageManager - класс для получения различных видов информации, связанной с пакетами приложений, которые в настоящее время установлены на устройстве.
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
                        ////queryIntentActivities - Получить все действия, которые могут быть выполнены для данного намерения
        Collections.sort(activities, new Comparator<ResolveInfo>() {                            //В общем эта штука сравнивает текстовые метки разных ResolveInfo
            @Override
            public int compare(ResolveInfo t1, ResolveInfo t2) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        t1.loadLabel(pm).toString(),
                        t2.loadLabel(pm).toString()
                );
            }
        });
        /*Collections - последовательные наборы элементов (родительский класс для Set, List, Queue). Определяет основные методы работы с простыми наборами элементов (size(), isEmpty(), add(E e) и др.)
        *Comparator - Функция сравнения, которая накладывает общий порядок на некоторые коллекция объектов. Компараторы могут быть переданы в метод сортировки
        *ResolveInfo объект для всех действий, которые соответствуют предоставленной Intent
        * loadLabel - Извлеките текущую текстовую метку, связанную с этим элементом.*/
        Log.i(TAG, "Found " + activities.size() + " activities.");

        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }
}
