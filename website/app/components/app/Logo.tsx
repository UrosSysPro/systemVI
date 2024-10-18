import {ReactElement} from "react";

export function Logo():ReactElement {
    return <div className={`flex items-center`}>
        <span className={"font-mono pr-0.5"}>system</span>
        <span className={"font-mono pr-0.5"}>VI</span>

        {/*<span className={"px-1.5 py-0.25 rounded-sm bg-green-400 font-mono shadow-sm text-sm "}>VI</span>*/}
    </div>;
}