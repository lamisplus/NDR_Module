import React, { forwardRef, useEffect, useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import * as api from "./../../../api";
import axios from "axios";
import { Modal, ModalBody, ModalHeader } from "reactstrap";
import { Link } from "react-router-dom";
import MaterialTable from "material-table";
import CloudUpload from "@material-ui/icons/CloudUpload";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";
import ErrorIcon from "@mui/icons-material/Error";
import { FiUploadCloud } from "react-icons/fi";
import FileSaver from "file-saver";
import "semantic-ui-css/semantic.min.css";
import { Dropdown, Button as Buuton2, Menu, Icon } from "semantic-ui-react";
import { token as token } from "./../../../api";
import { ToastContainer, toast } from "react-toastify";
import ProgressBar from "react-bootstrap/ProgressBar";

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
import moment from "moment";

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

export default function DownloadNdr(props) {
  const classes = useStyles();
  const [generatedNdrListed, setGeneratedNdrList] = useState([]);
  const [loading, setLoading] = useState("");
  const [modal, setModal] = useState(false);
  const toggle = () => setModal(!modal);

  useEffect(() => {
    generatedNdrList();
  }, []);

  //Load NDR WEB in the modal\
  const loadNdrWeb = () => {
    toggle();
  };

  ///GET LIST OF FACILITIES
  async function generatedNdrList() {
    axios
      .get(`${api.url}ndr/files`, {
        headers: { Authorization: `Bearer ${api.token}` },
      })
      .then((response) => {
        //console.log(response.data);
        setGeneratedNdrList(response.data);
        //console.log(response.data);
      })
      .catch((error) => {});
  }

  const gotoErrorTable = (id) => {
    axios
      .get(`${api.url}ndr/file/error/${id}`, {
        headers: { Authorization: `Bearer ${api.token}` },
      })
      .then((response) => {
        //console.log(response.data);
        props.setErrors(response.data);
        //console.log(response.data);
      })
      .catch((error) => {});
    props.setValue(4);
  };

  const downloadFile = (fileName) => {
    if (fileName.includes("redacted")) {
      axios
        .get(`${api.url}ndr/download_redacted/${fileName}`, {
          headers: { Authorization: `Bearer ${api.token}` },
          responseType: "blob",
        })
        .then((response) => {
          const responseData = response.data;
          let blob = new Blob([responseData], {
            type: "application/octet-stream",
          });
          FileSaver.saveAs(blob, `${fileName}.zip`);
        })
        .catch((error) => {});
    } else {
      axios
        .get(`${api.url}ndr/download/${fileName}`, {
          headers: { Authorization: `Bearer ${api.token}` },
          responseType: "blob",
        })
        .then((response) => {
          const responseData = response.data;
          let blob = new Blob([responseData], {
            type: "application/octet-stream",
          });
          FileSaver.saveAs(blob, `${fileName}.zip`);
        })
        .catch((error) => {});
    }
  };
  const generateAction = (ndrFileId) => {
    setModal(true);
    //const fileID ={id: ndrFileId }
    //SENDING A POST REQUEST
    axios
      .get(`${api.url}ndr-emr/ndr-auto-pusher?id=${ndrFileId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        window.setTimeout(() => {
          toast.success(" Uploading To NDR Successful!");
          setModal(false);
          generatedNdrList();
        }, 5000);

        //props.history.push("/generate", { state: 'download'});
      })
      .catch((error) => {
        setModal(false);
        //toast.error(" Something went wrong!");
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

  const varient = (value) => {
    //console.log(value);
    if (value <= 20) {
      return "danger";
    } else if (value > 20 && value <= 69) {
      return "warning";
    } else if (value >= 70 && value <= 99) {
      return "info";
    } else if (value === 100) {
      return "success";
    } else {
      return "success";
    }
  };

  return (
    <div>
      <ToastContainer autoClose={3000} hideProgressBar />
      <Button
        variant="contained"
        color="primary"
        className=" float-right"
        startIcon={<FiUploadCloud size="10" />}
        style={{ backgroundColor: "#014d88" }}
        href="https://ndr.phis3project.org.ng/Identity/Account/Login?ReturnUrl=%2F"
        //onClick={loadNdrWeb}
        target="_blank"
      >
        <span> NDR Portal</span>
      </Button>

      <br />
      <br />
      <MaterialTable
        icons={tableIcons}
        title="List of Files Generated"
        columns={[
          { title: "Facility Name", field: "name", filtering: false },
          {
            title: "Number of Files Generated",
            field: "files",
            filtering: false,
          },
          { title: "File Name", field: "fileName", filtering: false },
          {
            title: "Date Last Generated",
            field: "date",
            type: "date",
            filtering: false,
          },
          {
            title: "NDR Upload Status",
            field: "ndrStatus",
            type: "date",
            filtering: false,
          },

          {
            title: "Action",
            field: "actions",
            filtering: false,
          },
        ]}
        isLoading={loading}
        data={generatedNdrListed.map((row) => ({
          name: row.facility,
          files: row.files,
          fileName: row.fileName,
          date: moment(row.lastModified).format("LLLL"),
          ndrStatus: (
            <ProgressBar
              now={row.percentagePushed}
              variant={varient(row.percentagePushed)}
              label={`${row.percentagePushed}%`}
            />
          ),
          actions: (
            <div>
              <Menu.Menu position="right">
                <Menu.Item>
                  <Buuton2
                    style={{ backgroundColor: "rgb(153,46,98)" }}
                    primary
                  >
                    <Dropdown item text="Action">
                      <Dropdown.Menu style={{ marginTop: "10px" }}>
                        <Dropdown.Item
                          onClick={() => downloadFile(row.fileName)}
                        >
                          <CloudDownloadIcon color="primary" /> Download File
                        </Dropdown.Item>
                        <Dropdown.Item onClick={() => gotoErrorTable(row.id)}>
                          <ErrorIcon color="error" />
                          Error File
                        </Dropdown.Item>
                        {!row.completelyPushed && (
                          <Dropdown.Item onClick={() => generateAction(row.id)}>
                            <CloudUpload color="primary" /> Upload To NDR
                          </Dropdown.Item>
                        )}
                      </Dropdown.Menu>
                    </Dropdown>
                  </Buuton2>
                </Menu.Item>
              </Menu.Menu>
            </div>
          ),
        }))}
        options={{
          pageSizeOptions: [5, 10, 50, 100, 150, 500],
          headerStyle: {
            backgroundColor: "#014d88",
            color: "#fff",
            margin: "auto",
          },
          filtering: true,
          searchFieldStyle: {
            width: "300%",
            margingLeft: "250px",
          },
          exportButton: false,
          searchFieldAlignment: "left",
        }}
      />

      <Modal
        isOpen={modal}
        toggle={toggle}
        backdrop={false}
        fade={true}
        style={{ marginTop: "250px" }}
        size="lg"
      >
        <ModalBody>
          <h1>Uploading File To NDR. Please wait...</h1>
        </ModalBody>
      </Modal>
    </div>
  );
}
