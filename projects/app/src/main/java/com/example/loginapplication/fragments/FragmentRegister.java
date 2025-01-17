package com.example.loginapplication.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginapplication.R;
import com.example.loginapplication.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegister#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegister extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentRegister() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRegister.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRegister newInstance(String param1, String param2) {
        FragmentRegister fragment = new FragmentRegister();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button registerBtn = view.findViewById(R.id.buttonFragRegRegister);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                TextView error = view.findViewById(R.id.textViewFragRegError);
                // email
                EditText emailEditText = view.findViewById(R.id.textFragRegEmailAddress);
                String emailContent = emailEditText.getText().toString();

                // password
                EditText passwordEditText = view.findViewById(R.id.textFragRegPassword);
                String password = passwordEditText.getText().toString();

                // verify password
                EditText passwordVerifyEditText = view.findViewById(R.id.textFragRegPasswordVerify);
                String passwordVerify = passwordVerifyEditText.getText().toString();

                // phone
                EditText phoneEditText = view.findViewById(R.id.textFragRegPhone);
                String phone = phoneEditText.getText().toString();

                if (emailContent == null || password == null || passwordVerify == null || phone == null)
                {
                    error.setText("One field or more are missing!");
                    valid = false;
                }

//                if (emailContent.isEmpty() || password.isEmpty() || passwordVerify.isEmpty() || phone.isEmpty())
//                {
//                    error.setText("One field or more are missing!");
//                    valid = false;
//                }

                if (!password.equals(passwordVerify))
                {
                    error.setText("Invalid password validation!");
                    valid = false;
                }

                if (valid)
                {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    assert mainActivity != null;
                    mainActivity.register();
                    Navigation.findNavController(view).navigate(R.id.mainFragment);
                }
            }
        });

        return view;
    }
}