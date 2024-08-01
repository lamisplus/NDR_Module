import React, { useState, useEffect, forwardRef } from "react";
import MaterialTable, { MTableToolbar } from "material-table";
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
import { CardBody, Card, FormGroup, Label } from "reactstrap";
import { Alert, AlertTitle } from "@material-ui/lab";
import { GiFiles } from "react-icons/gi";
import AddBox from "@material-ui/icons/AddBox";
import ArrowUpward from "@material-ui/icons/ArrowUpward";
import Check from "@material-ui/icons/Check";
import ChevronLeft from "@material-ui/icons/ChevronLeft";
import ChevronRight from "@material-ui/icons/ChevronRight";
import Clear from "@material-ui/icons/Clear";
import DeleteOutline from "@material-ui/icons/DeleteOutline";
import Edit from "@material-ui/icons/Edit";
import FilterList from "@material-ui/icons/FilterList";
import FirstPage from "@material-ui/icons/FirstPage";
import LastPage from "@material-ui/icons/LastPage";
import Remove from "@material-ui/icons/Remove";
import SaveAlt from "@material-ui/icons/SaveAlt";
import Search from "@material-ui/icons/Search";
import ViewColumn from "@material-ui/icons/ViewColumn";
import Button from "@material-ui/core/Button";
import AccountBalanceIcon from "@material-ui/icons/AccountBalance";
import { Modal, ModalBody } from "reactstrap";
import { Link } from "react-router-dom";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import { TiArrowForward } from "react-icons/ti";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
import uniq from "lodash/uniq";
//import { Label } from "semantic-ui-react";
import { MdDashboard } from "react-icons/md";
//import { Spinner } from "reactstrap";

const tableIcons = {
  Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
  Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
  Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
  Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
  DetailPanel: forwardRef((props, ref) => (
    <ChevronRight {...props} ref={ref} />
  )),
  Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
  Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
  Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
  FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
  LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
  NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
  PreviousPage: forwardRef((props, ref) => (
    <ChevronLeft {...props} ref={ref} />
  )),
  ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
  Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
  SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref} />),
  ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
  ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />),
};

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

export default function GenerateNdr(props) {
  const classes = useStyles();
  const [facilities, setFacilities] = useState([]);
  const [processing, setProcessing] = useState(false);
  const [facilitiesApi, setfacilitiesApi] = useState({ facilities: [] });
  const [status, setStatus] = useState(false);
  const [checked, setChecked] = React.useState([]);
  const [user, setUser] = useState(null);
  const [modal, setModal] = useState(false);
  const toggle = () => setModal(!modal);
  const [showPPI, setShowPPI] = useState(true);
  const [selectPatients, setSelectPatients] = useState([]);
  const patientIDs = [];
  let currentDate = new Date().toISOString().split("T")[0];
  const [objValues, setObjValues] = useState({
    startDate: "",
    endDate: "",
  });
  const [selectedRows, setSelectedRows] = useState([]);

  const handleCheckBox = (e) => {
    if (e.target.checked) {
      setShowPPI(false);
    } else {
      setShowPPI(true);
    }
  };

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
    setSelectedRows([]);
    let FacilityIDArray = "";

    checked.forEach(function (value) {
      const id = value.organisationUnitId;
      const facilityparam = "facilityIds=" + id;
      FacilityIDArray = facilityparam;
    });

    facilitiesApi["facilities"] = FacilityIDArray;

    if (selectPatients.length > 0) {
      axios
        .get(
          `${api.url}ndr/generate/patients?${FacilityIDArray}&initial=${status}&patientIds=${selectPatients}`,
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .then((response) => {
          setSelectPatients([]);
          window.setTimeout(() => {
            toast.success(" Generating NDR Successful!");
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
    } else if (objValues.startDate !== "" && objValues.endDate !== "") {
      axios
        .get(
          `${api.url}ndr/optimization/date-range?${FacilityIDArray}&startDate=${objValues.startDate}&endDate=${objValues.endDate}`,
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .then((response) => {
          window.setTimeout(() => {
            toast.success(" Generating NDR Successful!");
            setModal(false);
            props.setValue(3);
          }, 5000);
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
    } else {
      axios
        .get(
          `${api.url}ndr/optimization?${FacilityIDArray}&isInitial=${status}`,
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .then((response) => {
          window.setTimeout(() => {
            toast.success(" Generating NDR Successful!");
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
    }
  };

  const handleRowSelection = (rows) => {
    setSelectedRows(rows);
    let patientIDs = uniq(rows).map((row) => row.uuid);
    setSelectPatients(patientIDs);
  };

  const handleInputChange = (e) => {
    console.log(e.target.innerText, e.target.name, e.target.name);
    setObjValues({
      ...objValues,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <div>
      <ToastContainer autoClose={3000} hideProgressBar />
      <Card>
        <CardBody>
          <>
            <Alert severity="info">
              <AlertTitle>Info</AlertTitle>
              Please check the Facilities you want
            </Alert>
            <br />
            <form>
              <div className="row">
                <div className="form-group  col-md-2">
                  <b>
                    <p>Generate by date range</p>
                  </b>
                </div>
                <div className="form-group  col-md-5">
                  <FormGroup>
                    <Label>From *</Label>
                    <input
                      type="date"
                      className="form-control"
                      name="startDate"
                      id="startDate"
                      min={"1980-01-01"}
                      max={currentDate}
                      value={objValues.startDate}
                      onChange={handleInputChange}
                      style={{
                        border: "1px solid #014D88",
                        borderRadius: "0.2rem",
                      }}
                    />
                  </FormGroup>
                </div>
                <div className="form-group  col-md-5">
                  <FormGroup>
                    <Label>To *</Label>
                    <input
                      type="date"
                      className="form-control"
                      name="endDate"
                      id="endDate"
                      min={"1980-01-01"}
                      max={currentDate}
                      //min={objValues.startDate}
                      value={objValues.endDate}
                      onChange={handleInputChange}
                      style={{
                        border: "1px solid #014D88",
                        borderRadius: "0.2rem",
                      }}
                    />
                  </FormGroup>
                </div>
              </div>
            </form>
            <br />
            <div className="row">
              <div className="form-group  col-md-2"></div>
              <div className="form-group  col-md-2">
                <label>
                  <input
                    type="radio"
                    name="status"
                    checked={status === false}
                    onChange={handleChange}
                  />
                  <b> Updated</b>
                </label>
              </div>
              <div className="form-group  col-md-2">
                <label>
                  <input
                    type="radio"
                    name="status"
                    checked={status === true}
                    onChange={handleChange}
                  />
                  <b> Initial</b>
                </label>
              </div>
              <div className="form-group  col-md-3">
                <label>
                  <List dense className={classes.root}>
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
                </label>
              </div>
              <div className="form-group  col-md-3">
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
              </div>
            </div>

            <div>
              <MaterialTable
                icons={tableIcons}
                title="Eligible Patients"
                columns={[
                  // { title: " ID", field: "Id" },
                  {
                    title: "Patient Name",
                    field: "name",
                    hidden: showPPI,
                  },
                  { title: "Unique ID", field: "uniqueId", filtering: false },
                  { title: "Sex", field: "sex", filtering: false },
                  { title: "Patient UUID", field: "uuid", filtering: false },
                  //{ title: "Age", field: "age", filtering: false },
                  //{ title: "Enrollment Status", field: "v_status", filtering: false },
                  //{ title: "ART Number", field: "v_status", filtering: false },
                  { title: "ART Status", field: "status", filtering: false },
                ]}
                data={(query) =>
                  new Promise((resolve, reject) =>
                    axios
                      .get(
                        `${baseUrl}hiv/patient/enrollment/list?pageSize=${query.pageSize}&pageNo=${query.page}&searchValue=${query.search}`,
                        { headers: { Authorization: `Bearer ${token}` } }
                      )
                      .then((response) => response)
                      .then((result) => {
                        resolve({
                          data: result.data.records.map((row) => ({
                            name:
                              row.currentStatus !== "Not Enrolled" ? (
                                <>
                                  <Link
                                    to={{
                                      //pathname: "/patient-history",
                                      state: { patientObj: row },
                                    }}
                                    title={"Click to view patient dashboard"}
                                  >
                                    {" "}
                                    {row.firstName + " " + row.surname}
                                  </Link>
                                </>
                              ) : (
                                <>
                                  <Link
                                    to={{
                                      //pathname: "/enroll-patient",
                                      state: {
                                        patientId: row.id,
                                        patientObj: row,
                                      },
                                    }}
                                    title={"Enroll Patient"}
                                  >
                                    {" "}
                                    {row.firstName + " " + row.surname}
                                  </Link>
                                </>
                              ),
                            uniqueId: row.uniqueId,
                            sex: row.sex,
                            uuid: row.personUuid,
                            //age: calculate_age(row.dateOfBirth),

                            status: (
                              <Label color="blue" size="mini">
                                {row.currentStatus}
                              </Label>
                            ),
                          })),
                          page: query.page,
                          totalCount: result.data.totalRecords,
                        });
                      })
                      .then(() => {
                        setSelectedRows([]);
                      })
                  )
                }
                options={{
                  search: true,
                  selection: true,
                  headerStyle: {
                    backgroundColor: "#014d88",
                    color: "#fff",
                  },
                  searchFieldStyle: {
                    width: "300%",
                    margingLeft: "300px",
                  },
                  rowStyle: (rowData) => ({
                    backgroundColor: selectedRows.find(
                      (row) => row.tableData.id === rowData.tableData.id
                    )
                      ? "#B4D3B2"
                      : "",
                  }),
                  filtering: false,
                  exportButton: false,
                  searchFieldAlignment: "left",
                  pageSizeOptions: [10, 20, 100],
                  pageSize: 10,
                  debounceInterval: 400,
                }}
                components={{
                  Toolbar: (props) => (
                    <div>
                      <div className="form-check custom-checkbox  float-left mt-4 ml-3 ">
                        <input
                          type="checkbox"
                          className="form-check-input"
                          name="showPP!"
                          id="showPP"
                          value="showPP"
                          checked={showPPI === true ? false : true}
                          onChange={handleCheckBox}
                          style={{
                            border: "1px solid #014D88",
                            borderRadius: "0.25rem",
                          }}
                        />
                        <label
                          className="form-check-label"
                          htmlFor="basic_checkbox_1"
                        >
                          <b style={{ color: "#014d88", fontWeight: "bold" }}>
                            SHOW PII
                          </b>
                        </label>
                      </div>
                      <MTableToolbar {...props} />
                    </div>
                  ),
                }}
                onSelectionChange={handleRowSelection}
              />
            </div>
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
          <h2>Generating NDR File. Please wait...</h2>
        </ModalBody>
      </Modal>
    </div>
  );
}
