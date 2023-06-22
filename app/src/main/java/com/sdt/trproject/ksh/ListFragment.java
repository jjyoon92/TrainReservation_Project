package com.sdt.trproject.ksh;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sdt.trproject.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment implements View.OnClickListener {
    private BoardListAdapter mBoardAdapter;
    private EditText mSearchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mBoardAdapter = new BoardListAdapter();
        mRecyclerView.setAdapter(mBoardAdapter);

        ImageButton mSearchButton = view.findViewById(R.id.search_btn);
        mSearchText = view.findViewById(R.id.search_edt);
        mSearchButton.setOnClickListener(this);

        refresh();


        mBoardAdapter.setOnItemClickListener(new BoardListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, BoardVo data) {
                Activity activity = requireActivity();
                if (!(activity instanceof BoardActivity)) {
                    return;
                }
                ((BoardActivity) activity).navigate(DetailFragment.newInstance(data.getIndex()));
            }
        });
    }


    public void refresh() {

        BoardService apiService = RetrofitService.getApiService();
        String searchText = mSearchText.getText().toString();

        Call<ResponseVo> mCall = searchText.length() == 0 ? apiService.get_board() : apiService.get_board_search(new BoardVo(searchText));
        mCall.enqueue(new Callback<>() {
            //콜백 받는 부분
            @Override
            public void onResponse(@NonNull Call<ResponseVo> call, @NonNull Response<ResponseVo> response) {
                ResponseVo responses = response.body();
                if (responses.getResult().equals("success") && responses.getDataList().size() != 0) {
                    List<BoardVo> boardVo = responses.getDataList();
                    mBoardAdapter.addAll(boardVo);
                } else {
                    Toast.makeText(requireContext(), "정보 없다", Toast.LENGTH_LONG).show();
                    mBoardAdapter.clear();
                }
            }

            @Override
            public void onFailure(Call<ResponseVo> call, Throwable t) {
                Toast.makeText(requireContext(), "재요청", Toast.LENGTH_LONG).show();
                System.out.println("hello");
                System.out.println(t.getMessage());
                t.printStackTrace();
                //mBoardAdapter.clear();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.search_btn:
                refresh();
                break;
        }
    }

}