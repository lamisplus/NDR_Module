import React, { useState, useEffect } from "react";
import { Row, Col, CardBody, Card } from "reactstrap";
import Button from "@material-ui/core/Button";
import axios from "axios";
import * as api from "./../../../api";
import { token as token, url as baseUrl } from "./../../../api";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import AccountBalanceIcon from "@material-ui/icons/AccountBalance";
import Checkbox from "@material-ui/core/Checkbox";
import { ToastContainer, toast } from "react-toastify";
import CircularProgress from "@mui/material/CircularProgress";

const Biometrics = (props) => {
  const [facilities, setFacilities] = useState([]);
  const [checked, setChecked] = React.useState([]);
  const [modal, setModal] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [facilitiesApi, setfacilitiesApi] = useState({ facilities: [] });

  async function fetchMe() {
    axios
      .get(`${api.url}account`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        //setUser(response.data);
        setFacilities(response.data.applicationUserOrganisationUnits);
      })
      .catch((error) => {});
  }

  useEffect(() => {
    fetchMe();
  }, []);

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

  const generateBiometrics = () => {
    setProcessing(true);
    setModal(true);
    let FacilityIDArray = "";

    checked.forEach(function (value) {
      const id = value.organisationUnitId;
      const facilityparam = "facilityIds=" + id;
      FacilityIDArray = facilityparam;
      //FacilityIDArray.push(id);
    });

    console.log(FacilityIDArray);

    facilitiesApi["facilities"] = FacilityIDArray;

    axios
      .get(`${api.url}ndr/recapture/generate?${FacilityIDArray}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        window.setTimeout(() => {
          toast.success(" Generating Biometrics XML Successful!");
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
    <Card>
      <CardBody>
        <Button
          color="primary"
          variant="contained"
          className=" float-right mr-1"
          size="large"
          onClick={() => generateBiometrics()}
          //disabled="true"
        >
          <span style={{ textTransform: "capitalize" }}>
            {" "}
            Generate Biometrics XML{" "}
          </span>
        </Button>
        <br />
        {processing ? <CircularProgress /> : ""}
        {facilities.map((value) => {
          //console.log(value)
          const labelId = `checkbox-list-secondary-label-${value.id}`;
          return (
            <Row>
              <Col>
                <List key={value.id}>
                  <ListItem key={value.id}>
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
                </List>
              </Col>
              <Col></Col>
              <Col></Col>
              <Col></Col>
            </Row>
          );
        })}
      </CardBody>
    </Card>
  );
};

export default Biometrics;
