import React from "react";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import IconButton from "@material-ui/core/IconButton";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import BorrowForm from "./BorrowForm";
import {useDispatch} from "react-redux";
import {borrowBook, deleteBook, getBooks, returnBook} from "../store/book/bookActions";
import {getStudents} from "../store/student/studentActions";

const BookOptions = ({bookId, borrowed}) => {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const handleClick = event => {
        setAnchorEl(event.currentTarget);
    };

    const dispatch = useDispatch();
    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleReturnBook = () => {
        handleClose();
        dispatch(returnBook(bookId));
    }

    const handleDeleteBook = () => {
        handleClose();
        dispatch(deleteBook(bookId));
    }

    return (
        <>
            <IconButton onClick={handleClick}>
                <MoreVertIcon/>
            </IconButton>
            <Menu
                id="simple-menu"
                anchorEl={anchorEl}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={handleClose}
            >
                {!borrowed ? <MenuItem onClick={handleClose}>
                        <BorrowForm
                            bookId={bookId}
                        />
                    </MenuItem>
                    :
                    <MenuItem onClick={handleReturnBook}>
                        Return book
                    </MenuItem>
                }
                <MenuItem disabled={borrowed} onClick={handleDeleteBook}>
                    Delete book
                </MenuItem>
            </Menu>
        </>
    );
};

export default BookOptions;
