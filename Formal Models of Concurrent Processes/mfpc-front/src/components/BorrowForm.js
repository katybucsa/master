import React, {useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import {connect, useDispatch, useSelector} from "react-redux";
import {getStudents} from "../store/student/studentActions";
import {borrowBook} from "../store/book/bookActions";
import PropTypes from "prop-types";
import Select from 'react-select';

const useStyles = makeStyles(theme => ({
    container: {
        flexWrap: "wrap",
        height: 200,
    },
    button: {
        textTransform: "none"
    }
}));

const BorrowForm = ({getStudents, loading, bookId}) => {
    const classes = useStyles();
    const [open, setOpen] = useState(false);
    const [studentId, setStudentId] = useState("");

    const dispatch = useDispatch();

    const students = useSelector(state => {
        return state.students.data;
    });

    const onChange = (event) => {
        setStudentId(event.value);
    }

    const handleClickOpen = () => {
        if (loading)
            getStudents()
        setOpen(true);
    };

    const handleClose = (event, reason) => {
        if (reason === 'backdropClick' || reason === 'escapeKeyDown') {
            return false;
        }

        setOpen(false);
        if (event.currentTarget.value === 'ok')
            dispatch(borrowBook(bookId, studentId));
    };

    return (
        <div>
            <Button className={classes.button} onClick={handleClickOpen}>Borrow book</Button>
            <Dialog
                fullWidth
                open={open}
                onClose={handleClose}
            >
                <DialogTitle>Fill the form</DialogTitle>
                <DialogContent>
                    <form className={classes.container} noValidate>
                        <Select
                            options={students && students.map(st => ({
                                label: `${st.firstName} ${st.lastName} - ${st.studentId}`,
                                value: st.studentId
                            }))}
                            onChange={onChange}
                        />
                    </form>
                </DialogContent>
                <DialogActions>
                    <Button value={'cancel'} onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button value={'ok'} onClick={handleClose} color="primary">
                        Ok
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
}

BorrowForm.propTypes = {
    getStudents: PropTypes.func.isRequired,
    bookId: PropTypes.number,
    loading: PropTypes.bool,
};

const mapStateToProps = state => ({
    loading: state.students.loading,
});

export default connect(mapStateToProps, {
    getStudents,
})(BorrowForm);
