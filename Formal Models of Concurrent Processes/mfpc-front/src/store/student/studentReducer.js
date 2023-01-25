import {ADD_STUDENT, DELETE_STUDENT, GET_REQUEST_FAIL, GET_STUDENTS} from './studentActions';

const initialState = {
    loading: true,
    data: []
};

// eslint-disable-next-line import/no-anonymous-default-export
export default function (state = initialState, action) {
    const {type, payload} = action;

    switch (type) {
        case GET_STUDENTS:
            return {
                ...state,
                loading: false,
                data: payload
            };
        case ADD_STUDENT:
            return {
                ...state,
                loading: true
            }
        case DELETE_STUDENT:
            return {
                ...state,
                loading: true
            }
        case GET_REQUEST_FAIL:
        default:
            return state;

    }
}
