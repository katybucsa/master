import React, {useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import {useDispatch} from "react-redux";
import {Input} from "@material-ui/core";
import {addAuthor} from "../store/authors/authorActions";

const useStyles = makeStyles(theme => ({
    container: {
        flexWrap: "wrap",
        height: 200,
    },
    button: {
        textTransform: "none",
        color: "white",

    }
}));

const AddAuthorForm = () => {
    const classes = useStyles();
    const [open, setOpen] = useState(false);
    const [authorName, setAuthorName] = useState("");

    const dispatch = useDispatch();

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleChangeName = (event) => {
        setAuthorName(event.target.value);
    }

    const handleClose = (event, reason) => {
        if (reason === 'backdropClick' || reason === 'escapeKeyDown') {
            return false;
        }

        setOpen(false);
        if (event.currentTarget.value === 'ok')
            dispatch(addAuthor(authorName));
    };

    return (
        <div>
            <Button className={classes.button} onClick={handleClickOpen}>Add New Author</Button>
            <Dialog
                fullWidth
                open={open}
                onClose={handleClose}
            >
                <DialogTitle>Fill the form</DialogTitle>
                <DialogContent>
                    <form className={classes.container} noValidate>
                        <Input
                            placeholder="Author Name"
                            inputProps={{"aria-label": "description"}}
                            onChange={handleChangeName}
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

export default AddAuthorForm;
