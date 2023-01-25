import {ADD_AUTHOR, GET_AUTHORS, GET_REQUEST_FAIL} from "./authorActions";

const initialState = {
    loading: true,
    data: []
};

// eslint-disable-next-line import/no-anonymous-default-export
export default function (state = initialState, action) {
    const {type, payload} = action;


    switch (type) {
        case GET_AUTHORS:
            return {
                ...state,
                loading: false,
                data: payload
            };
        case ADD_AUTHOR:
            return {
                ...state,
                loading: true
            }
        case GET_REQUEST_FAIL:
        default:
            return state;

    }
}
