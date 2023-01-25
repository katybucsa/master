import React, {useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import {useDispatch} from "react-redux";
import {Input} from "@material-ui/core";
import {addStudent} from "../store/student/studentActions";

const useStyles = makeStyles(theme => ({
    container: {
        flexWrap: "wrap",
        height: 200,
    },
    button: {
        textTransform: "none",
        color: "black",
        background: '#007bff'
    }
}));

const FormAddStudent = () => {
    const classes = useStyles();
    const [open, setOpen] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");

    const dispatch = useDispatch();

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = (event, reason) => {
        if (reason === 'backdropClick' || reason === 'escapeKeyDown') {
            return false;
        }

        setOpen(false);
        if (event.currentTarget.value === 'ok')
            dispatch(addStudent(firstName, lastName, email));
    };

    const handleChangeFirstName = (event) => {
        setFirstName(event.target.value);
    }

    const handleChangeLastName = (event) => {
        setLastName(event.target.value);
    }

    const handleChangeEmail = (event) => {
        setEmail(event.target.value);
    }

    return (
        <div>
            <Button className={classes.button} onClick={handleClickOpen}>Add New Student</Button>
            <Dialog
                fullWidth
                open={open}
                onClose={handleClose}
            >
                <DialogTitle>Fill the form</DialogTitle>
                <DialogContent>
                    <form className={classes.container} noValidate>
                        <Input
                            placeholder="First Name"
                            inputProps={{"aria-label": "description"}}
                            onChange={handleChangeFirstName}
                        />
                        <br/>
                        <br/>
                        <Input
                            placeholder="Last Name"
                            inputProps={{"aria-label": "description"}}
                            onChange={handleChangeLastName}
                        />
                        <br/>
                        <br/>
                        <Input
                            placeholder="Email"
                            inputProps={{"aria-label": "description"}}
                            onChange={handleChangeEmail}
                        />
                    </form>
                </DialogContent>
                <DialogActions>
                    <Button value={'cancel'} onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button value={'ok'} onClick={handleClose} color="primary">
                        Add Student
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
}

export default FormAddStudent;
