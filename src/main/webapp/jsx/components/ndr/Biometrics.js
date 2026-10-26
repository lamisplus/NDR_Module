import React, { useState, useEffect, useCallback, useMemo } from "react";
import { Row, Col, CardBody, Card } from "reactstrap";
import Button from "@material-ui/core/Button";
import { makeStyles } from "@material-ui/core/styles";
import axios from "axios";
import * as api from "./../../../api";
import { token as token, url as baseUrl } from "./../../../api";
import AccountBalanceIcon from "@material-ui/icons/AccountBalance";
import FingerprintIcon from "@mui/icons-material/Fingerprint";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Alert, AlertTitle } from "@material-ui/lab";
import CircularProgress from "@mui/material/CircularProgress";
import { Modal, ModalBody } from "reactstrap";

// ---------- Styles ----------
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
  sectionTitle: {
    fontSize: "0.95rem",
    fontWeight: 600,
    color: "#1a237e",
    marginBottom: theme.spacing(1),
    display: "flex",
    alignItems: "center",
    gap: 6,
  },
  facilityPanel: {
    backgroundColor: "#fff",
    border: "1px solid #e8eaf6",
    borderRadius: 8,
    padding: theme.spacing(1),
    maxHeight: 360,
    overflowY: "auto",
  },
  facilityItem: {
    display: "flex",
    alignItems: "center",
    padding: "10px 12px",
    borderRadius: 6,
    cursor: "pointer",
    transition: "background-color 0.15s ease",
    marginBottom: 4,
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
  emptyState: {
    padding: theme.spacing(5),
    textAlign: "center",
    color: "#9e9e9e",
    fontSize: "0.875rem",
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
}));

const Biometrics = (props) => {
  const classes = useStyles();
  const [facilities, setFacilities] = useState([]);
  const [checked, setChecked] = useState([]);
  const [modal, setModal] = useState(false);
  const [processing, setProcessing] = useState(false);

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

  const facilityQueryString = useMemo(() => {
    return checked
      .map((value) => `facilityIds=${value.organisationUnitId}`)
      .join("&");
  }, [checked]);

  const generateBiometrics = useCallback(async () => {
    if (!facilityQueryString) {
      toast.warn("Please select at least one facility.");
      return;
    }

    setProcessing(true);
    setModal(true);

    try {
      await axios.get(
        `${api.url}ndr/recapture/generate?${facilityQueryString}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        },
      );

      window.setTimeout(() => {
        toast.success("Generating Biometrics XML Successful!");
        setModal(false);
        setProcessing(false);
        setChecked([]);
        props.setValue(5);
      }, 3000);
    } catch (error) {
      setModal(false);
      setProcessing(false);

      const errorMessage =
        error?.response?.data?.apierror?.message?.trim() ||
        "Something went wrong. Please try again.";

      toast.error(errorMessage);
    }
  }, [facilityQueryString, props]);

  const canGenerate = checked.length >= 1 && !processing;
  const hasFacilities = facilities.length > 0;

  return (
    <div>
      <ToastContainer autoClose={3000} hideProgressBar />

      <Card className={classes.card}>
        <CardBody>
          {/* Info alert */}
          <Alert severity="info">
            <AlertTitle>Generate Biometrics Recapture XML</AlertTitle>
            Select the facilities whose biometrics you want to recapture, then
            click <strong>Generate Biometrics XML</strong>. You'll be redirected
            to the Downloads tab once the file is ready.
          </Alert>

          {/* Header row */}
          <div className={classes.headerRow} style={{ marginTop: 24 }}>
            <div className={classes.sectionTitle}>
              <AccountBalanceIcon style={{ fontSize: 18 }} />
              Select Facilities
              {checked.length > 0 && (
                <span className={classes.selectedCounter}>
                  {checked.length} selected
                </span>
              )}
            </div>

            <Button
              variant="contained"
              className={classes.generateBtn}
              disabled={!canGenerate}
              onClick={generateBiometrics}
              startIcon={
                processing ? (
                  <CircularProgress size={18} style={{ color: "#fff" }} />
                ) : (
                  <FingerprintIcon />
                )
              }
            >
              {processing ? "Generating..." : "Generate Biometrics XML"}
            </Button>
          </div>

          <div className={classes.facilityPanel}>
            {hasFacilities ? (
              facilities.map((value) => {
                const isChecked = checked.indexOf(value) !== -1;
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
                      backgroundColor: isChecked ? "#e8eaf6" : undefined,
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
                      {value.organisationUnitName}
                    </span>
                  </div>
                );
              })
            ) : (
              <div className={classes.emptyState}>
                No facilities available for your account
              </div>
            )}
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
            Generating Biometrics XML
          </div>
          <div className={classes.modalSubtext}>
            Please wait while we process your request. This may take a few
            moments.
          </div>
        </ModalBody>
      </Modal>
    </div>
  );
};

export default Biometrics;
