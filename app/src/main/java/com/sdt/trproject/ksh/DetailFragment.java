package com.sdt.trproject.ksh;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdt.trproject.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    private static final String ARG_POSITION = "ARG_ARG_POSITION";
    private static final String ARG_ARRAY_INDEX = "ARG_ARRAY_INDEX";

    private  int mPosition;
    private ArrayList<Integer> mIntegerArrayList;
    private AppCompatButton backButton;
    private ImageView pastButton;
    private ImageView nextButton;
    private TextView title;
    private TextView content;


    public DetailFragment() {
    }

    public static DetailFragment newInstance(int position, ArrayList<Integer> integerArrayList) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_POSITION, position);
        args.putIntegerArrayList(ARG_ARRAY_INDEX,integerArrayList);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPosition = bundle.getInt(ARG_POSITION);
            mIntegerArrayList = bundle.getIntegerArrayList(ARG_ARRAY_INDEX);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);

        backButton = view.findViewById(R.id.btnList);
        pastButton = view.findViewById(R.id.btnPast);
        nextButton = view.findViewById(R.id.btnNext);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = requireActivity();
                if (!(activity instanceof BoardActivity)) {
                    return;
                }
                ((BoardActivity) activity).navigateToBack();
            }
        });
        pastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --mPosition;
                refresh();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++mPosition;
                refresh();
            }
        });

        refresh();

    }

    public void refresh() {

        BoardService apiService = RetrofitService.getApiService();

        Call<ResponseVo> mCall = apiService.get_board_index(new BoardVo(mIntegerArrayList.get(mPosition)));
        mCall.enqueue(new Callback<>() {
            //콜백 받는 부분
            @Override
            public void onResponse(@NonNull Call<ResponseVo> call, @NonNull Response<ResponseVo> response) {
                ResponseVo responses = response.body();
                if (responses.getResult().equals("success") && responses.getDataList().size() != 0) {
                    List<BoardVo> boardVo = responses.getDataList();

                    title.setText(boardVo.get(0).getTitle());
                    content.setText(boardVo.get(0).getContent());



                    if (mIntegerArrayList.get(mPosition).equals(mIntegerArrayList.get(0))) {
                        pastButton.setVisibility(View.INVISIBLE);
                    } else {
                        pastButton.setVisibility(View.VISIBLE);
                    }
                    if (mIntegerArrayList.get(mPosition).equals(mIntegerArrayList.get(mIntegerArrayList.size() - 1))) {
                        nextButton.setVisibility(View.INVISIBLE);
                    } else {
                        nextButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(requireContext(), "정보 없다", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseVo> call, Throwable t) {
                Toast.makeText(requireContext(), "알 수 없는 오류입니다. 재요청 부탁드립니다.", Toast.LENGTH_LONG).show();

                System.out.println(t.getMessage());
                t.printStackTrace();

            }
        });
    }

}