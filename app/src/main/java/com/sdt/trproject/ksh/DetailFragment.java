package com.sdt.trproject.ksh;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sdt.trproject.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {


    private static final String ARG_INPUT_INDEX = "ARG_INPUT_INDEX";

    private int mIndex;
    private Button backButton;
    private TextView title;
    private TextView content;

    public DetailFragment() { }

    public static DetailFragment newInstance(int index) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INPUT_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIndex = getArguments().getInt(ARG_INPUT_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_answer_detail, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title= view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);

        backButton = view.findViewById(R.id.btnList);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = requireActivity();
                if(!(activity instanceof BoardActivity)) {
                    return;
                }
                ((BoardActivity) activity).navigateToBack();
            }
        });
        refresh();

    }

    public void refresh() {

        BoardService apiService = RetrofitService.getApiService();


        Call<ResponseVo> mCall = apiService.get_board_index(new BoardVo(mIndex));
        mCall.enqueue(new Callback<>() {
            //콜백 받는 부분
            @Override
            public void onResponse(@NonNull Call<ResponseVo> call, @NonNull Response<ResponseVo> response) {
                ResponseVo responses = response.body();
                if (responses.getResult().equals("success") && responses.getDataList().size() != 0) {
                    List<BoardVo> boardVo = responses.getDataList();

                    title.setText(boardVo.get(0).getTitle());
                    content.setText(boardVo.get(0).getContent());
                } else {
                    Toast.makeText(requireContext(), "정보 없다", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseVo> call, Throwable t) {
                Toast.makeText(requireContext(), "재요청", Toast.LENGTH_LONG).show();

                System.out.println(t.getMessage());
                t.printStackTrace();
                //mBoardAdapter.clear();
            }
        });
    }

}