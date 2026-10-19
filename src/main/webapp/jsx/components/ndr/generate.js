import React, {
  useState,
  useEffect,
  useCallback,
  useMemo,
  forwardRef,
} from "react";
import MaterialTable, { MTableToolbar } from "material-table";
import { makeStyles } from "@material-ui/core/styles";
import axios from "axios";
import * as api from "./../../../api";
import { token as token, url as baseUrl } from "./../../../api";
import { CardBody, Card, FormGroup, Label } from "reactstrap";
import { Alert, AlertTitle } from "@material-ui/lab";
import { GiFiles } from "react-icons/gi";
import Button from "@material-ui/core/Button";
import AccountBalanceIcon from "@material-ui/icons/AccountBalance";
import CalendarTodayIcon from "@material-ui/icons/CalendarToday";
import InfoOutlinedIcon from "@material-ui/icons/InfoOutlined";
import { Modal, ModalBody } from "reactstrap";
import { Link } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
import uniq from "lodash/uniq";
import CircularProgress from "@mui/material/CircularProgress";
import {
  AddBox,
  ArrowUpward,
  Check,
  ChevronLeft,
  ChevronRight,
  Clear,
  DeleteOutline,
  Edit,
  FilterList,
  FirstPage,
  LastPage,
  Remove,
  SaveAlt,
  Search,
  ViewColumn,
} from "@material-ui/icons";

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
  card: {
    border: "none",
    borderRadius: 10,
    boxShadow: "0 2px 12px rgba(0,0,0,0.06)",
  },
  headerRow: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    flexWrap: "wrap",
    gap: theme.spacing(2),
    marginBottom: theme.spacing(3),
  },
  sectionCard: {
    backgroundColor: "#f8f9ff",
    border: "1px solid #e8eaf6",
    borderRadius: 8,
    padding: theme.spacing(2),
    marginBottom: theme.spacing(2),
  },
  sectionTitle: {
    fontSize: "0.95rem",
    fontWeight: 600,
    color: "#1a237e",
    marginBottom: theme.spacing(1.5),
    display: "flex",
    alignItems: "center",
    gap: 6,
  },
  dateInput: {
    width: "100%",
    padding: "10px 12px",
    border: "1px solid #d1d5db",
    borderRadius: 8,
    fontSize: "0.875rem",
    color: "#333",
    outline: "none",
    transition: "border-color 0.15s ease",
    "&:focus": {
      borderColor: "#014d88",
      boxShadow: "0 0 0 3px rgba(1,77,136,0.1)",
    },
  },
  radioGroup: {
    display: "flex",
    gap: theme.spacing(3),
    padding: theme.spacing(1.5, 2),
    backgroundColor: "#fff",
    borderRadius: 8,
    border: "1px solid #e8eaf6",
  },
  radioLabel: {
    display: "flex",
    alignItems: "center",
    gap: 8,
    cursor: "pointer",
    fontSize: "0.875rem",
    fontWeight: 500,
    color: "#333",
    margin: 0,
  },
  facilityItem: {
    display: "flex",
    alignItems: "center",
    padding: "10px 12px",
    borderRadius: 6,
    cursor: "pointer",
    backgroundColor: "#fff",
    border: "1px solid #e8eaf6",
    transition: "all 0.15s ease",
    "&:hover": {
      backgroundColor: "#f0f2ff",
    },
  },
  facilityIcon: {
    color: "#5c6bc0",
    fontSize: "1.25rem",
    marginRight: theme.spacing(1.5),
  },
  facilityName: {
    flex: 1,
    fontSize: "0.875rem",
    color: "#333",
    fontWeight: 500,
  },
  generateBtn: {
    backgroundColor: "#014d88",
    color: "#fff",
    textTransform: "none",
    fontWeight: 600,
    borderRadius: 8,
    padding: theme.spacing(1.2, 3),
    boxShadow: "0 2px 8px rgba(1,77,136,0.25)",
    "&:hover": {
      backgroundColor: "#013a66",
      boxShadow: "0 4px 12px rgba(1,77,136,0.35)",
    },
    "&:disabled": {
      backgroundColor: "#b0bec5",
      color: "#fff",
      boxShadow: "none",
    },
  },
  modalBody: {
    padding: theme.spacing(5),
    textAlign: "center",
  },
  modalTitle: {
    fontSize: "1.1rem",
    fontWeight: 600,
    color: "#1a237e",
    marginBottom: theme.spacing(1.5),
  },
  modalSubtext: {
    color: "#666",
    fontSize: "0.85rem",
  },
  selectedCounter: {
    display: "inline-flex",
    alignItems: "center",
    gap: 6,
    padding: "4px 12px",
    backgroundColor: "#e8eaf6",
    borderRadius: 20,
    fontSize: "0.78rem",
    fontWeight: 600,
    color: "#1a237e",
    marginLeft: theme.spacing(1),
  },
  modeHint: {
    fontSize: "0.75rem",
    color: "#5c6bc0",
    fontStyle: "italic",
    marginTop: theme.spacing(0.5),
  },
}));

export default function GenerateNdr(props) {
  const classes = useStyles();
  const [facilities, setFacilities] = useState([]);
  const [processing, setProcessing] = useState(false);
  const [status, setStatus] = useState(false); // false = Updated, true = Initial
  const [checked, setChecked] = useState([]);
  const [modal, setModal] = useState(false);
  const [showPPI, setShowPPI] = useState(false);
  const [selectPatients, setSelectPatients] = useState([]);
  const [selectedRows, setSelectedRows] = useState([]);

  const currentDate = useMemo(() => new Date().toISOString().split("T")[0], []);

  const [objValues, setObjValues] = useState({
    startDate: "",
    endDate: "",
  });

  useEffect(() => {
    let isMounted = true;
    axios
      .get(`${api.url}account`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        if (!isMounted) return;
        const { currentOrganisationUnitName, currentOrganisationUnitId } =
          response.data;
        setFacilities([
          {
            facility: currentOrganisationUnitName,
            facilityId: currentOrganisationUnitId,
          },
        ]);
      })
      .catch(() => {
        toast.error("Unable to load facilities. Please refresh the page.");
      });
    return () => {
      isMounted = false;
    };
  }, []);

  const handleToggle = useCallback(
    (value) => () => {
      setChecked((prev) => {
        const currentIndex = prev.indexOf(value);
        const newChecked = [...prev];
        if (currentIndex === -1) newChecked.push(value);
        else newChecked.splice(currentIndex, 1);
        return newChecked;
      });
    },
    [],
  );

  const facilityQueryString = useMemo(() => {
    return checked.map((value) => `facilityIds=${value.facilityId}`).join("&");
  }, [checked]);

  const handleRowSelection = useCallback((rows) => {
    setSelectedRows(rows);
    setSelectPatients(uniq(rows).map((row) => row.uuid));
  }, []);

  const handleInputChange = useCallback((e) => {
    const { name, value } = e.target;
    setObjValues((prev) => ({ ...prev, [name]: value }));
  }, []);

  const clearDateRange = useCallback(() => {
    setObjValues({ startDate: "", endDate: "" });
  }, []);

  const generationMode = useMemo(() => {
    if (selectPatients.length > 0) return "patients";
    if (objValues.startDate && objValues.endDate) return "date-range";
    return "default";
  }, [selectPatients, objValues]);

  const dateRangeInvalid = useMemo(() => {
    if (!objValues.startDate || !objValues.endDate) return false;
    return objValues.startDate > objValues.endDate;
  }, [objValues]);

  const generateAction = useCallback(async () => {
    if (!facilityQueryString) {
      toast.warn("Please select at least one facility.");
      return;
    }

    if (dateRangeInvalid) {
      toast.error("End date must be after start date.");
      return;
    }

    setProcessing(true);
    setModal(true);
    setSelectedRows([]);

    let url = "";
    let redirectTab = 4;

    if (generationMode === "patients") {
      url = `${api.url}ndr/generate/patients?${facilityQueryString}&initial=${status}&patientIds=${selectPatients}`;
    } else if (generationMode === "date-range") {
      url = `${api.url}ndr/optimization/date-range?${facilityQueryString}&startDate=${objValues.startDate}&endDate=${objValues.endDate}`;
    } else {
      url = `${api.url}ndr/optimization?${facilityQueryString}&isInitial=${status}`;
      redirectTab = 4;
    }

    try {
      await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      window.setTimeout(() => {
        toast.success("Generating NDR Successful!");
        setModal(false);
        setProcessing(false);
        setSelectPatients([]);
        props.setValue(redirectTab);
      }, 3000);
    } catch (error) {
      setModal(false);
      setProcessing(false);

      const errorMessage =
        error?.response?.data?.apierror?.message?.trim() ||
        "Something went wrong. Please try again.";

      toast.error(errorMessage);
    }
  }, [
    facilityQueryString,
    generationMode,
    selectPatients,
    objValues,
    status,
    dateRangeInvalid,
    props,
  ]);

  const canGenerate = checked.length >= 1 && !processing && !dateRangeInvalid;

  return (
    <div>
      <ToastContainer autoClose={3000} hideProgressBar />

      <Card className={classes.card}>
        <CardBody>
          {/* Info alert */}
          <Alert severity="info">
            <AlertTitle>Generate NDR</AlertTitle>
            Select facilities, choose a data type, and optionally provide a date
            range or select specific patients. You'll be redirected to the
            Downloads tab once the file is ready.
          </Alert>

          {/* ---------- Date Range Section ---------- */}
          <div className={classes.sectionCard} style={{ marginTop: 24 }}>
            <div className={classes.sectionTitle}>
              <CalendarTodayIcon style={{ fontSize: 18 }} />
              Optional: Generate by Date Range
            </div>
            <div className="row">
              <div className="col-md-5">
                <FormGroup>
                  <Label>From</Label>
                  <input
                    type="date"
                    className={classes.dateInput}
                    name="startDate"
                    id="startDate"
                    min="1980-01-01"
                    max={currentDate}
                    value={objValues.startDate}
                    onChange={handleInputChange}
                  />
                </FormGroup>
              </div>
              <div className="col-md-5">
                <FormGroup>
                  <Label>To</Label>
                  <input
                    type="date"
                    className={classes.dateInput}
                    name="endDate"
                    id="endDate"
                    min={objValues.startDate || "1980-01-01"}
                    max={currentDate}
                    value={objValues.endDate}
                    onChange={handleInputChange}
                  />
                </FormGroup>
              </div>
              <div className="col-md-2 d-flex align-items-center">
                {(objValues.startDate || objValues.endDate) && (
                  <Button
                    size="small"
                    onClick={clearDateRange}
                    style={{
                      textTransform: "none",
                      color: "#5c6bc0",
                      marginTop: 20,
                    }}
                  >
                    Clear dates
                  </Button>
                )}
              </div>
            </div>
            {dateRangeInvalid && (
              <div
                style={{
                  color: "#d32f2f",
                  fontSize: "0.78rem",
                  marginTop: 4,
                }}
              >
                End date must be after start date.
              </div>
            )}
            <div className={classes.modeHint}>
              {generationMode === "patients" &&
                "Mode: Selected patients (overrides date range)"}
              {generationMode === "date-range" && "Mode: Date range filter"}
              {generationMode === "default" &&
                "Mode: Default export (all eligible records)"}
            </div>
          </div>

          {/* ---------- Facilities + Type + Action ---------- */}
          <div className="row">
            {/* Facilities */}
            <div className="col-md-5">
              <div className={classes.sectionTitle}>
                <AccountBalanceIcon style={{ fontSize: 18 }} />
                Facilities
                {checked.length > 0 && (
                  <span className={classes.selectedCounter}>
                    {checked.length} selected
                  </span>
                )}
              </div>
              {facilities.length > 0 ? (
                facilities.map((value) => {
                  const isChecked = checked.indexOf(value) !== -1;
                  return (
                    <div
                      key={value.facilityId}
                      className={classes.facilityItem}
                      onClick={handleToggle(value)}
                      role="button"
                      tabIndex={0}
                      onKeyDown={(e) =>
                        e.key === "Enter" && handleToggle(value)()
                      }
                      style={{
                        backgroundColor: isChecked ? "#e8eaf6" : undefined,
                        marginBottom: 8,
                      }}
                    >
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={handleToggle(value)}
                        onClick={(e) => e.stopPropagation()}
                        style={{ marginRight: 12, cursor: "pointer" }}
                      />
                      <AccountBalanceIcon className={classes.facilityIcon} />
                      <span className={classes.facilityName}>
                        {value.facility}
                      </span>
                    </div>
                  );
                })
              ) : (
                <div
                  style={{
                    padding: 24,
                    textAlign: "center",
                    color: "#9e9e9e",
                    fontSize: "0.85rem",
                  }}
                >
                  No facilities available
                </div>
              )}
            </div>

            {/* Data Type */}
            <div className="col-md-3">
              <div className={classes.sectionTitle}>Data Type</div>
              <div
                className={classes.radioGroup}
                style={{ flexDirection: "column", gap: 12 }}
              >
                <label className={classes.radioLabel}>
                  <input
                    type="radio"
                    name="status"
                    checked={status === false}
                    onChange={() => setStatus(false)}
                  />
                  Updated
                </label>
                <label className={classes.radioLabel}>
                  <input
                    type="radio"
                    name="status"
                    checked={status === true}
                    onChange={() => setStatus(true)}
                  />
                  Initial
                </label>
              </div>
            </div>

            {/* Action */}
            <div className="col-md-4 d-flex flex-column justify-content-between">
              <div className={classes.sectionTitle}>Action</div>
              <Button
                variant="contained"
                className={classes.generateBtn}
                disabled={!canGenerate}
                onClick={generateAction}
                startIcon={
                  processing ? (
                    <CircularProgress size={18} style={{ color: "#fff" }} />
                  ) : (
                    <GiFiles />
                  )
                }
              >
                {processing ? "Generating..." : "Generate NDR"}
              </Button>
              {selectPatients.length > 0 && (
                <div
                  style={{
                    fontSize: "0.78rem",
                    color: "#5c6bc0",
                    marginTop: 8,
                  }}
                >
                  {selectPatients.length} patient(s) selected — will override
                  date range
                </div>
              )}
            </div>
          </div>

          <div className="mt-4">
            <MaterialTable
              icons={tableIcons}
              title="Eligible Patients"
              columns={[
                { title: "Patient Name", field: "name", hidden: !showPPI },
                { title: "Unique ID", field: "uniqueId", filtering: false },
                { title: "Sex", field: "sex", filtering: false },
                { title: "Patient UUID", field: "uuid", filtering: false },
                { title: "ART Status", field: "status", filtering: false },
              ]}
              data={(query) =>
                new Promise((resolve) =>
                  axios
                    .get(
                      `${baseUrl}hiv/patient/enrollment/list?pageSize=${query.pageSize}&pageNo=${query.page}&searchValue=${query.search}`,
                      { headers: { Authorization: `Bearer ${token}` } },
                    )
                    .then((response) => response)
                    .then((result) => {
                      resolve({
                        data: result.data.records.map((row) => ({
                          name:
                            row.currentStatus !== "Not Enrolled" ? (
                              <Link
                                to={{ state: { patientObj: row } }}
                                title="Click to view patient dashboard"
                              >
                                {row.firstName + " " + row.surname}
                              </Link>
                            ) : (
                              <Link
                                to={{
                                  state: {
                                    patientId: row.id,
                                    patientObj: row,
                                  },
                                }}
                                title="Enroll Patient"
                              >
                                {row.firstName + " " + row.surname}
                              </Link>
                            ),
                          uniqueId: row.uniqueId,
                          sex: row.sex,
                          uuid: row.personUuid,
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
                    .catch(() => {
                      resolve({ data: [], page: 0, totalCount: 0 });
                      toast.error("Failed to load patients.");
                    })
                    .finally(() => setSelectedRows([])),
                )
              }
              options={{
                search: true,
                selection: true,
                headerStyle: {
                  backgroundColor: "#014d88",
                  color: "#fff",
                  fontWeight: 600,
                },
                rowStyle: (rowData) => ({
                  backgroundColor: selectedRows.find(
                    (row) => row.tableData.id === rowData.tableData.id,
                  )
                    ? "#e8f5e9"
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
                Toolbar: (toolbarProps) => (
                  <div>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        padding: "12px 16px",
                        gap: 8,
                      }}
                    >
                      <input
                        type="checkbox"
                        id="showPPI"
                        checked={showPPI}
                        onChange={(e) => setShowPPI(e.target.checked)}
                        style={{ cursor: "pointer" }}
                      />
                      <label
                        htmlFor="showPPI"
                        style={{
                          color: "#014d88",
                          fontWeight: 600,
                          fontSize: "0.8rem",
                          cursor: "pointer",
                          margin: 0,
                        }}
                      >
                        SHOW PII
                      </label>
                    </div>
                    <MTableToolbar {...toolbarProps} />
                  </div>
                ),
              }}
              onSelectionChange={handleRowSelection}
            />
          </div>
        </CardBody>
      </Card>

      <Modal
        isOpen={modal}
        toggle={() => setModal(false)}
        backdrop="static"
        centered
        size="md"
      >
        <ModalBody className={classes.modalBody}>
          <CircularProgress size={48} style={{ color: "#014d88" }} />
          <div className={classes.modalTitle} style={{ marginTop: 20 }}>
            Generating NDR File
          </div>
          <div className={classes.modalSubtext}>
            Please wait while we process your request. This may take a few
            moments.
          </div>
        </ModalBody>
      </Modal>
    </div>
  );
}
