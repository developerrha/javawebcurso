/* 
 *Homzode
 */
var back_login="";
var flag=false;
function FactoryXMLHttpRequest(){
    if( window.XMLHttpRequest ){ 
        return new XMLHttpRequest();
    }else if( window.ActiveXObject ){ 
        var msxmls = new Array(
                     'Msxml2.XMLHTTP.5.0',
                     'Msxml2.XMLHTTP.4.0',
                     'Msxml2.XMLHTTP.3.0',
                     'Msxml2.XMLHTTP',
                     'Microsoft.XMLHTTP');
        for(var i=0;i<msxmls.length;i++){
            try{
                return new ActiveXObject(msxmls[i]);
            }catch(e){
            }
        }   
    }throw new Error("No pudo instanciar XMLHttpRequest");
}


function showAlert(){
    alert("Entrada a la pagina por JavaScript");    
}


function sendTask(url,mode,source,e){
    var xmlhttp = FactoryXMLHttpRequest();

    if(mode==="gest_private"){
        if(xmlhttp){ 
            xmlhttp.open('POST',url,false);
            xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            var str_data="";
            str_data += "source="+ source;

            var getString = url.split('?')[1];
            // obtenemos un array con cada clave=valor 
            var GET = getString.split('&');
            var get = {};
            var ide="";
            // recorremos todo el array de valores
            for(var i = 0, l = GET.length; i < l; i++){
                var tmp = GET[i].split('=');
                get[tmp[0]] = unescape(decodeURI(tmp[1]));
                var name=unescape(decodeURI(tmp[0]));
                var value=unescape(decodeURI(tmp[1]));

                str_data +="&"+name+"="+value;
            }
            var clave=((window.document).getElementById("pinreq_text")).value;
            //alert("clave: "+clave);
            str_data +="&clave="+clave;

            xmlhttp.send(str_data);
            var temp = xmlhttp.responseText;

            //(window.document).appendChild()
            ((window.document).getElementById("business_guest")).innerHTML=temp;
        }
    }

    if(mode==="newuser_guest"){
        if(xmlhttp){ 
            xmlhttp.open('POST',url,false);
            xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
            var str_data="";
            str_data += "source="+ source;
            xmlhttp.send(str_data);
            var temp = xmlhttp.responseText;
            ((window.document).getElementById("business_guest_new")).innerHTML=temp;
            ((window.document).getElementById("business_guest_new")).style.visibility = "visible";
        }
    }
}
    
    