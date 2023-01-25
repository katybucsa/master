import axios from "axios";
import {setAlert} from "../alert/alertActions";

export const ADD_AUTHOR = "ADD_AUTHOR";
export const GET_AUTHORS = "GET_AUTHORS";
export const GET_REQUEST_FAIL = "GET_REQUEST_FAIL";


export const addAuthor = (name) => async dispatch => {

    const body = {
        name: name
    };
    try {
        axios.post('/authors', body).then(res => {
                dispatch({
                    type: ADD_AUTHOR,
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

export const getAuthors = () => async dispatch => {
    try {
        axios.get('/authors').then(res => {
                dispatch({
                    type: GET_AUTHORS,
                    payload: res.data.data
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
