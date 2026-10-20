import React, {
  useState,
  useEffect,
  forwardRef,
  useCallback,
  useMemo,
} from "react";
import { makeStyles } from "@material-ui/core/styles";
import axios from "axios";
import * as api from "./../../../api";
import { token as token, url as baseUrl } from "./../../../api";
import { CardBody, Card } from "reactstrap";
import { Alert, AlertTitle } from "@material-ui/lab";
import { GiFiles } from "react-icons/gi";
import Button from "@material-ui/core/Button";
import AccountBalanceIcon from "@material-ui/icons/AccountBalance";
import MaterialTable, { MTableToolbar } from "material-table";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
import { Modal, ModalBody } from "reactstrap";
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
import uniq from "lodash/uniq";

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
  },
  card: {
    border: "none",
    borderRadius: 10,
    boxShadow: "0 2px 12px rgba(0,0,0,0.06)",
  },
  sectionTitle: {
    fontSize: "0.95rem",
    fontWeight: 600,
    color: "#1a237e",
    marginBottom: theme.spacing(1),
    display: "flex",
    alignItems: "center",
    gap: 6,
  },
  radioGroup: {
    display: "flex",
    gap: theme.spacing(3),
    padding: theme.spacing(1.5, 2),
    backgroundColor: "#f8f9ff",
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
  facilityPanel: {
    backgroundColor: "#fff",
    border: "1px solid #e8eaf6",
    borderRadius: 8,
    padding: theme.spacing(1),
    maxHeight: 280,
    overflowY: "auto",
  },
  facilityItem: {
    borderRadius: 6,
    marginBottom: 4,
    "&:hover": {
      backgroundColor: "#f0f2ff",
    },
  },
  facilityIcon: {
    minWidth: 36,
    color: "#5c6bc0",
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
    },
  },
  emptyState: {
    padding: theme.spacing(4),
    textAlign: "center",
    color: "#9e9e9e",
    fontSize: "0.85rem",
  },
  modalBody: {
    padding: theme.spacing(4),
    textAlign: "center",
  },
  modalTitle: {
    fontSize: "1.1rem",
    fontWeight: 600,
    color: "#1a237e",
    marginBottom: theme.spacing(2),
  },
  spinner: {
    width: 40,
    height: 40,
    margin: "0 auto 16px",
    border: "4px solid #e3f2fd",
    borderTop: "4px solid #014d88",
    borderRadius: "50%",
    animation: "$spin 1s linear infinite",
  },
  "@keyframes spin": {
    "0%": { transform: "rotate(0deg)" },
    "100%": { transform: "rotate(360deg)" },
  },
  errorAlert: {
    marginTop: theme.spacing(2),
  },
}));

export default function Prep(props) {
  const classes = useStyles();
  const [facilities, setFacilities] = useState([]);
  const [processing, setProcessing] = useState(false);
  const [status, setStatus] = useState(false); // false = Updated, true = Initial
  const [checked, setChecked] = useState([]);
  const [modal, setModal] = useState(false);
  const [selectedRows, setSelectedRows] = useState([]);
  const [selectPatients, setSelectPatients] = useState([]);

  const toggle = useCallback(() => setModal((m) => !m), []);

  useEffect(() => {
    let isMounted = true;
    axios
      .get(`${api.url}account`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        if (!isMounted) return;
        setFacilities(response.data.applicationUserOrganisationUnits || []);
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

  const handleRowSelection = useCallback((rows) => {
    setSelectedRows(rows);
    setSelectPatients(uniq(rows).map((row) => row.personUuid));
  }, []);

  const facilityQueryString = useMemo(() => {
    return checked
      .map((value) => `facilityIds=${value.organisationUnitId}`)
      .join("&");
  }, [checked]);

  const generateAction = useCallback(async () => {
    if (!facilityQueryString) return;

    setProcessing(true);
    setModal(true);

    const usePatients = selectPatients.length > 0;
    const url = usePatients
      ? `${api.url}ndr/generate/prep?${facilityQueryString}&isInitial=${status}&patientIds=${selectPatients}`
      : `${api.url}ndr/prep?${facilityQueryString}&isInitial=${status}`;

    try {
      await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      window.setTimeout(() => {
        toast.success("Generating PrEP XML Successful!");
        setModal(false);
        setProcessing(false);
        setSelectPatients([]);
        props.setValue(4);
      }, 3000);
    } catch (error) {
      setModal(false);
      setProcessing(false);

      const errorMessage =
        error?.response?.data?.apierror?.message?.trim() ||
        "Something went wrong. Please try again.";

      toast.error(errorMessage);
    }
  }, [facilityQueryString, selectPatients, status, props]);

  const hasFacilities = facilities.length > 0;
  const canGenerate = checked.length >= 1 && !processing;

  return (
    <div>
      <ToastContainer autoClose={3000} hideProgressBar />

      <Card className={classes.card}>
        <CardBody>
          {/* Info alert */}
          <Alert severity="info">
            <AlertTitle>Generate PrEP XML</AlertTitle>
            Select one or more facilities below, choose the data type, then
            click
            <strong> Generate PrEP XML</strong>. You may optionally select
            specific patients from the table to limit the export.
          </Alert>

          <div className="row mt-4">
            {/* Data Type */}
            <div className="col-md-4">
              <div className={classes.sectionTitle}>Data Type</div>
              <div className={classes.radioGroup}>
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

            {/* Facilities */}
            <div className="col-md-4">
              <div className={classes.sectionTitle}>
                <AccountBalanceIcon style={{ fontSize: 16 }} />
                Facilities ({checked.length} selected)
              </div>
              <div className={classes.facilityPanel}>
                {hasFacilities ? (
                  facilities.map((value) => {
                    const labelId = `facility-${value.id}`;
                    return (
                      <div
                        key={value.id}
                        className={classes.facilityItem}
                        onClick={handleToggle(value)}
                        role="button"
                        tabIndex={0}
                        onKeyDown={(e) =>
                          e.key === "Enter" && handleToggle(value)()
                        }
                        style={{
                          display: "flex",
                          alignItems: "center",
                          padding: "6px 10px",
                          cursor: "pointer",
                        }}
                      >
                        <input
                          type="checkbox"
                          checked={checked.indexOf(value) !== -1}
                          onChange={handleToggle(value)}
                          onClick={(e) => e.stopPropagation()}
                          style={{ marginRight: 10, cursor: "pointer" }}
                        />
                        <span style={{ fontSize: "0.85rem", color: "#333" }}>
                          {value.organisationUnitName}
                        </span>
                      </div>
                    );
                  })
                ) : (
                  <div className={classes.emptyState}>
                    No facilities available
                  </div>
                )}
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
                startIcon={<GiFiles />}
              >
                {processing ? "Generating..." : "Generate Prep XML"}
              </Button>
              {selectPatients.length > 0 && (
                <div
                  style={{
                    fontSize: "0.78rem",
                    color: "#5c6bc0",
                    marginTop: 8,
                  }}
                >
                  {selectPatients.length} patient(s) selected for export
                </div>
              )}
            </div>
          </div>

          <div className="mt-4">
            <MaterialTable
              icons={tableIcons}
              title="Eligible Patients"
              columns={[
                { title: "Patient Id", field: "personId", filtering: false },
                { title: "Patient Uuid", field: "personUuid", filtering: false },
                {
                  title: "Hospital No",
                  field: "hospitalNumber",
                  filtering: false,
                },
              ]}
              data={(query) =>
                new Promise((resolve, reject) =>
                  axios
                    .get(
                      `${baseUrl}prep/persons/enrolled?pageSize=${query.pageSize}&pageNo=${query.page}&searchValue=${query.search}`,
                      { headers: { Authorization: `Bearer ${token}` } },
                    )
                    .then((response) => response)
                    .then((result) => {
                      resolve({
                        data: result.data.records.map((row) => ({
                          personId: row.personId,
                          personUuid: row.personUuid,
                          hospitalNumber: row.hospitalNumber,
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
                toolbar: true,
              }}
              components={{
                Toolbar: (toolbarProps) => (
                  <div>
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
        toggle={toggle}
        backdrop="static"
        centered
        size="md"
      >
        <ModalBody className={classes.modalBody}>
          <div className={classes.spinner} />
          <div className={classes.modalTitle}>Generating PrEP XML Files</div>
          <div style={{ color: "#666", fontSize: "0.85rem" }}>
            Please wait while we process your request. This may take a few
            moments.
          </div>
        </ModalBody>
      </Modal>
    </div>
  );
}
