import React, {useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import {connect, useDispatch} from "react-redux";
import {Input} from "@material-ui/core";
import Select from "react-select";
import PropTypes from "prop-types";
import {addBook} from "../store/book/bookActions";
import {getAuthors} from "../store/authors/authorActions";

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

const AddBookForm = ({getAuthors, authors, loading}) => {
    const classes = useStyles();
    const [open, setOpen] = useState(false);
    const [name, setName] = useState("");
    const [pageCount, setPageCount] = useState(0);
    const [authorId, setAuthorId] = useState(null);

    const dispatch = useDispatch();

    if (loading)
        getAuthors();

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = (event, reason) => {
        if (reason === 'backdropClick' || reason === 'escapeKeyDown') {
            return false;
        }

        setOpen(false);
        if (event.currentTarget.value === 'ok')
            dispatch(addBook(name, pageCount, authorId));
    };

    const handleChangeName = (event) => {
        setName(event.target.value);
    }

    const handleChangePageCount = (event) => {
        setPageCount(Number(event.target.value) || null);
    }

    const handleChangeAuthorId = (event) => {
        setAuthorId(Number(event.value) || null);
    }

    return (
        <div>
            <Button className={classes.button} onClick={handleClickOpen}>Add New Book</Button>
            <Dialog
                fullWidth
                open={open}
                onClose={handleClose}
            >
                <DialogTitle>Fill the form</DialogTitle>
                <DialogContent>
                    <form className={classes.container} noValidate>
                        <Input
                            placeholder="Book Name"
                            inputProps={{"aria-label": "description"}}
                            onChange={handleChangeName}
                        />
                        <br/>
                        <br/>
                        <Input
                            placeholder="Page Count"
                            inputProps={{"aria-label": "description"}}
                            onChange={handleChangePageCount}
                        />
                        <br/>
                        <br/>
                        <div style={{width: '200px'}}>
                            <Select
                                options={authors && authors.map(a => ({
                                    label: a.name,
                                    value: a.authorId
                                }))}
                                onChange={handleChangeAuthorId}
                            />
                        </div>
                    </form>
                </DialogContent>
                <DialogActions>
                    <Button value={'cancel'} onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button value={'ok'} onClick={handleClose} color="primary">
                        Add Book
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
}

AddBookForm.propTypes = {
    getAuthors: PropTypes.func.isRequired,
    loading: PropTypes.bool,
    authors: PropTypes.array,
};

const mapStateToProps = state => ({
    authors: state.authors.data,
    loading: state.authors.loading,
});

export default connect(mapStateToProps, {
    getAuthors,
})(AddBookForm);
