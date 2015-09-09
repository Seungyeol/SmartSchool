package com.aura.smartschool.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.R;
import com.aura.smartschool.adapter.QnAAdapter;
import com.aura.smartschool.dialog.LoadingDialog;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.vo.BoardVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SeungyeolBak on 15. 8. 14..
 */
public class QnAListFragment extends Fragment {


    private ArrayList<BoardVO> qnAList;

    private View btnQnA;
    private RecyclerView mQnAListView;
    private QnAAdapter mQnAAdapter;

    public QnAListFragment() {
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qna_list, container, false);

        btnQnA = v.findViewById(R.id.btn_add_qna);
        btnQnA.setOnClickListener(qnaClicked);

        qnAList = new ArrayList<>();

        mQnAListView = (RecyclerView) v.findViewById(R.id.qna_list);
        mQnAListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mQnAAdapter = new QnAAdapter();
        mQnAListView.setAdapter(mQnAAdapter);

        loadAppNotice();

        return v;
    }

    private void loadAppNotice() {
        LoadingDialog.showLoading(getActivity());
        String url = Constant.HOST + Constant.API_GET_BOARD_LIST;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("member_id", LoginManager.getInstance().getLoginUser().member_id);
            jsonObject.put("board_type", 1);
            new AQuery(getActivity()).post(url, jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    LoadingDialog.hideLoading();
                    try {
                        if (status.getCode() != 200) {
                            showToast("질문 리스트 가져오기에 실패했습니다.");
                            return;
                        }

                        SchoolLog.d("LDK", "result:" + object.toString(1));

                        if ("0".equals(object.getString("result"))) {
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                BoardVO qna = new BoardVO();
                                qna.boardId = json.getInt("board_id");
                                qna.memberId = json.getInt("member_id");
                                qna.boardType = json.getInt("board_type");
                                qna.title = json.getString("title");
                                qna.content = json.getString("content");
                                qna.answer = json.getString("answer");
                                qna.created = json.getString("created");
                                qna.updated = json.getString("updated");
                                qnAList.add(qna);
                            }
                            mQnAAdapter.setQnAList(qnAList);
                            mQnAAdapter.notifyDataSetChanged();
                        } else {
                            showToast("질문 리스트 가져오기에 실패했습니다.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("질문 리스트 가져오기에 실패했습니다.");
                    }
                }
            });
        }catch (JSONException e) {

        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener qnaClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new QnASendFragment()).addToBackStack(null).commit();
        }
    };
}
