import axios from "axios";
import {setAlert} from "../alert/alertActions";

export const GET_STUDENTS = "GET_STUDENTS";
export const DELETE_STUDENT = "DELETE_STUDENT";
export const ADD_STUDENT = "ADD_STUDENT";
export const GET_REQUEST_FAIL = "GET_REQUEST_FAIL";

export const getStudents = () => async dispatch => {
    try {
        const res = await axios.get(
            `students`
        );
        dispatch({
            type: GET_STUDENTS,
            payload: res.data.data
        });
    } catch (err) {
        const status = err.response.status;
        if (status === 400) {
            dispatch(setAlert("Try again", "danger"));
        }
        dispatch({
            type: GET_REQUEST_FAIL,
            payload: err
        });
    }
};

export const addStudent = (firstName, lastName, email) => async dispatch => {

    const body = {
        firstName: firstName,
        lastName: lastName,
        email: email
    };
    try {
        axios.post('/students', body).then(res => {
                dispatch({
                    type: ADD_STUDENT,
                    payload: res.data
                })
            }
        );

    } catch (err) {
        const status = err.response.status;
        if (status === 400) {
            dispatch(setAlert("Try again", "danger"));
        }
        dispatch({
            type: GET_REQUEST_FAIL,
            payload: err
        });
    }
};

export const deleteStudent = (studentId) => async dispatch => {

    try {
        axios.delete(`/students/${studentId}`).then(res => {
                dispatch({
                    type: DELETE_STUDENT,
                    payload: res.data
                })
            }
        );

    } catch (err) {
        const status = err.response.status;
        if (status === 400) {
            dispatch(setAlert("Try again", "danger"));
        }
        dispatch({
            type: GET_REQUEST_FAIL,
            payload: err
        });
    }
};
