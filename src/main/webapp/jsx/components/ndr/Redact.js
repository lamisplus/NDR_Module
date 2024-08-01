import React, { useState, useEffect } from "react";
import { makeStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Checkbox from "@material-ui/core/Checkbox";
import axios from "axios";
import * as api from "./../../../api";
import { token as token, url as baseUrl } from "./../../../api";
import { CardBody, Card } from "reactstrap";
import { Alert, AlertTitle } from "@material-ui/lab";
import { GiFiles } from "react-icons/gi";
import Button from "@material-ui/core/Button";
import AccountBalanceIcon from "@material-ui/icons/AccountBalance";
import { Modal, ModalBody } from "reactstrap";
//import { useHistory } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
//import { Spinner } from "reactstrap";

const useStyles = makeStyles((theme) => ({
  root: {
    width: "100%",
    maxWidth: 360,
    backgroundColor: theme.palette.background.paper,
    "& > * + *": {
      marginTop: theme.spacing(2),
    },
  },
}));

export default function Redact(props) {
  //const [loading, stillLoading] = useState(true)
  //let history = useHistory();
  const classes = useStyles();
  const [facilities, setFacilities] = useState([]);
  const [processing, setProcessing] = useState(false);
  const [facilitiesApi, setfacilitiesApi] = useState({ facilities: [] });
  const [status, setStatus] = useState(false);
  const [checked, setChecked] = React.useState([]);
  const [user, setUser] = useState(null);
  const [modal, setModal] = useState(false);
  const toggle = () => setModal(!modal);

  useEffect(() => {
    fetchMe();
  }, []);

  ///GET LIST OF FACILITIES
  async function fetchMe() {
    axios
      .get(`${api.url}account`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        setUser(response.data);
        setFacilities(response.data.applicationUserOrganisationUnits);
      })
      .catch((error) => {});
  }

  //GET LIST OF NDR GENERATED
  const handleToggle = (value) => () => {
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    setChecked(newChecked);
  };

  const handleChange = (event) => {
    setStatus(!status);
  };
  const generateAction = () => {
    setProcessing(true);
    setModal(true);
    let FacilityIDArray = "";
    //LOOPING THROUGH THE FACILITIES OBJECT ARRAY TO FORM THE NEW OBJECT
    checked.forEach(function (value) {
      const id = value.organisationUnitId;
      const facilityparam = "facilityIds=" + id;
      FacilityIDArray = facilityparam;
      //FacilityIDArray.push(id);
    });
    //console.log(FacilityIDArray)
    facilitiesApi["facilities"] = FacilityIDArray;
    //SENDING A POST REQUEST
    axios
      .get(
        `${api.url}ndr/redacted?${FacilityIDArray}&isInitial=${status}`,
        { headers: { Authorization: `Bearer ${token}` } }
      )
      .then((response) => {
        window.setTimeout(() => {
          toast.success(" Generating Redacted files Successfully!");
          setModal(false);
          props.setValue(3);
        }, 5000);

        //props.history.push("/generate", { state: 'download'});
      })
      .catch((error) => {
        setModal(false);
        setProcessing(false); // set the generate button true
        toast.error(" Something went wrong! Please contact administrator.");
        if (error.response && error.response.data) {
          let errorMessage =
            error.response.data.apierror &&
            error.response.data.apierror.message !== ""
              ? error.response.data.apierror.message
              : "Something went wrong, please try again";
          toast.error(errorMessage);
        } else {
          toast.error("Something went wrong. Please try again...");
        }
      });
  };

  return (
    <div>
      <ToastContainer autoClose={3000} hideProgressBar />
      <Card>
        <CardBody>
          {checked.length >= 1 ? (
            <>
              <Button
                color="primary"
                variant="contained"
                className=" float-right mr-1"
                size="large"
                hidden={processing}
                onClick={() => generateAction()}
              >
                {<GiFiles />} &nbsp;&nbsp;
                <span style={{ textTransform: "capitalize" }}>
                  {" "}
                  Generate Messages
                </span>
              </Button>
            </>
          ) : (
            <>
              <Button
                color="primary"
                variant="contained"
                className=" float-right mr-1"
                size="large"
                disabled="true"
              >
                {<GiFiles />} &nbsp;&nbsp;
                <span style={{ textTransform: "capitalize" }}>
                  {" "}
                  Generate Messages{" "}
                </span>
              </Button>
            </>
          )}

          <>
            <br /> <br />
            <Alert severity="info">
              <AlertTitle>Info</AlertTitle>
              Please check the Facilities you want
            </Alert>
            <br />
            <label>
              <input
                type="radio"
                name="status"
                checked={status === false}
                onChange={handleChange}
              />
              <b> Updated</b>
            </label>
            {"   "}
            {"   "}
            <label>
              <input
                type="radio"
                name="status"
                checked={status === true}
                onChange={handleChange}
              />
              <b> Initial</b>
            </label>
            <br />
            <List dense className={classes.root}>
              <br />

              {facilities.map((value) => {
                //console.log(value)
                const labelId = `checkbox-list-secondary-label-${value.id}`;
                return (
                  <ListItem key={value.id} button>
                    <ListItemAvatar>
                      <AccountBalanceIcon />
                    </ListItemAvatar>
                    <ListItemText
                      id={labelId}
                      primary={`${value.organisationUnitName}`}
                    />
                    <ListItemSecondaryAction>
                      <Checkbox
                        edge="end"
                        onChange={handleToggle(value)}
                        checked={checked.indexOf(value) !== -1}
                        inputProps={{ "aria-labelledby": labelId }}
                      />
                    </ListItemSecondaryAction>
                  </ListItem>
                );
              })}
            </List>
          </>
        </CardBody>
      </Card>
      <Modal
        isOpen={modal}
        toggle={toggle}
        backdrop={false}
        fade={true}
        style={{ marginTop: "250px" }}
        size="lg"
      >
        <ModalBody>
          <h2>Generating Redacted File. Please wait...</h2>
        </ModalBody>
      </Modal>
    </div>
  );
}
