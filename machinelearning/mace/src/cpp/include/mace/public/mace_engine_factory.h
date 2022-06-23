// Copyright 2018 The MACE Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// This is a generated file. DO NOT EDIT!

#ifndef MACE_CODEGEN_ENGINE_MACE_ENGINE_FACTORY_H_
#define MACE_CODEGEN_ENGINE_MACE_ENGINE_FACTORY_H_

#include <map>
#include <memory>
#include <string>
#include <vector>

#include "mace/public/mace.h"

namespace mace {

    namespace mobilenet_v1 {

        extern const unsigned char *LoadModelData();

        extern int64_t GetModelSize();

        extern const std::shared_ptr<MultiNetDef> CreateMultiNet();

        extern const std::string ModelName();

        extern const std::string ModelChecksum();

        extern const std::string ModelBuildTime();

        extern const std::string ModelBuildOptions();

    }  // namespace mobilenet_v1

    namespace mobilenet_v2 {

        extern const unsigned char *LoadModelData();

        extern int64_t GetModelSize();

        extern const std::shared_ptr<MultiNetDef> CreateMultiNet();

        extern const std::string ModelName();

        extern const std::string ModelChecksum();

        extern const std::string ModelBuildTime();

        extern const std::string ModelBuildOptions();

    }  // namespace mobilenet_v2

    namespace mobilenet_v1_quant {

        extern const unsigned char *LoadModelData();

        extern int64_t GetModelSize();

        extern const std::shared_ptr<MultiNetDef> CreateMultiNet();

        extern const std::string ModelName();

        extern const std::string ModelChecksum();

        extern const std::string ModelBuildTime();

        extern const std::string ModelBuildOptions();

    }  // namespace mobilenet_v1_quant

    namespace mobilenet_v2_quant {

        extern const unsigned char *LoadModelData();

        extern int64_t GetModelSize();

        extern const std::shared_ptr<MultiNetDef> CreateMultiNet();

        extern const std::string ModelName();

        extern const std::string ModelChecksum();

        extern const std::string ModelBuildTime();

        extern const std::string ModelBuildOptions();

    }  // namespace mobilenet_v2_quant

    namespace {
        std::map<std::string, int> model_name_map{
                std::make_pair("mobilenet_v1", 0),
                std::make_pair("mobilenet_v2", 1),
                std::make_pair("mobilenet_v1_quant", 2),
                std::make_pair("mobilenet_v2_quant", 3),
        };
    }  // namespace

/// \brief Create MaceEngine from code
///
/// Create MaceEngine object based on model graph code and model data file or
/// model data code.
///
/// \param model_name[in]: the name of model you want to use.
/// \param model_data_file[in]: the path of model data file,
///        if model_data_format is code, just pass empty string("")
/// \param input_nodes[in]: the array of input nodes' name
/// \param output_nodes[in]: the array of output nodes' name
/// \param config[in]: configurations for MaceEngine.
/// \param engine[out]: output MaceEngine object
/// \param model_data_unused[out]: Indicates whether model_weights_data unused
/// \param tutor[in]: If tutor is not null, the current engine will use the
///                   tutor's runtimes, so that they will share the intermediate
///                   memory. You can use this mechanism to reduce the memory
///                   usage of multiple models in the same process, provided
///                   that the multiple models are running serially.
/// \return MaceStatus::MACE_SUCCESS for success, MACE_INVALID_ARGS for wrong arguments,
///         MACE_OUT_OF_RESOURCES for resources is out of range.
    __attribute__((deprecated)) MaceStatus CreateMaceEngineFromCode(
            const std::string &model_name,
            const std::string &model_data_file,
            const std::vector<std::string> &input_nodes,
            const std::vector<std::string> &output_nodes,
            const MaceEngineConfig &config,
            std::shared_ptr<MaceEngine> *engine,
            MaceEngine *tutor = nullptr) {
        // load model
        if (engine == nullptr) {
            return MaceStatus::MACE_INVALID_ARGS;
        }
        std::shared_ptr<MultiNetDef> multi_net_def;
        (void) model_data_file;
        const unsigned char *model_data;
        MaceStatus status = MaceStatus::MACE_SUCCESS;
        switch (model_name_map[model_name]) {
            case 0: {
                multi_net_def = mace::mobilenet_v1::CreateMultiNet();
                engine->reset(new mace::MaceEngine(config));
                model_data = mace::mobilenet_v1::LoadModelData();
                const int64_t model_size = mace::mobilenet_v1::GetModelSize();
                bool model_data_unused = false;
                status = (*engine)->Init(multi_net_def.get(), input_nodes, output_nodes,
                                         model_data, model_size, &model_data_unused,
                                         tutor);
                break;
            }
            case 1: {
                multi_net_def = mace::mobilenet_v2::CreateMultiNet();
                engine->reset(new mace::MaceEngine(config));
                model_data = mace::mobilenet_v2::LoadModelData();
                const int64_t model_size = mace::mobilenet_v2::GetModelSize();
                bool model_data_unused = false;
                status = (*engine)->Init(multi_net_def.get(), input_nodes, output_nodes,
                                         model_data, model_size, &model_data_unused,
                                         tutor);
                break;
            }
            case 2: {
                multi_net_def = mace::mobilenet_v1_quant::CreateMultiNet();
                engine->reset(new mace::MaceEngine(config));
                model_data = mace::mobilenet_v1_quant::LoadModelData();
                const int64_t model_size = mace::mobilenet_v1_quant::GetModelSize();
                bool model_data_unused = false;
                status = (*engine)->Init(multi_net_def.get(), input_nodes, output_nodes,
                                         model_data, model_size, &model_data_unused,
                                         tutor);
                break;
            }
            case 3: {
                multi_net_def = mace::mobilenet_v2_quant::CreateMultiNet();
                engine->reset(new mace::MaceEngine(config));
                model_data = mace::mobilenet_v2_quant::LoadModelData();
                const int64_t model_size = mace::mobilenet_v2_quant::GetModelSize();
                bool model_data_unused = false;
                status = (*engine)->Init(multi_net_def.get(), input_nodes, output_nodes,
                                         model_data, model_size, &model_data_unused,
                                         tutor);
                break;
            }
            default:
                status = MaceStatus::MACE_INVALID_ARGS;
        }

        return status;
    }

/// \brief Create MaceEngine from code
///
/// Create MaceEngine object based on model graph code and model data code
///
/// \param model_name[in]: the name of model you want to use.
/// \param model_weights_data[in]: the content of model weights data, the
///                                returned engine will refer to this buffer
///                                if model_data_unused return false.
/// \param model_weights_data_size[in]: the size of model weights data
/// \param input_nodes[in]: the array of input nodes' name
/// \param output_nodes[in]: the array of output nodes' name
/// \param config[in]: configurations for MaceEngine.
/// \param engine[out]: output MaceEngine object
/// \param model_data_unused[out]: Indicates whether model_weights_data unused
/// \param tutor[in]: If tutor is not null, the current engine will use the
///                   tutor's runtimes, so that they will share the intermediate
///                   memory. You can use this mechanism to reduce the memory
///                   usage of multiple models in the same process, provided
///                   that the multiple models are running serially.
/// \return MaceStatus::MACE_SUCCESS for success,
///         MaceStatus::MACE_INVALID_ARGS for wrong arguments,
///         MaceStatus::MACE_OUT_OF_RESOURCES for resources is out of range.
    MaceStatus CreateMaceEngineFromCode(
            const std::string &model_name,
            const unsigned char *model_weights_data,
            const size_t model_weights_data_size,
            const std::vector<std::string> &input_nodes,
            const std::vector<std::string> &output_nodes,
            const MaceEngineConfig &config,
            std::shared_ptr<MaceEngine> *engine,
            bool *model_data_unused = nullptr,
            MaceEngine *tutor = nullptr,
            bool fake_warmup = false) {
        // load model
        if (engine == nullptr) {
            return MaceStatus::MACE_INVALID_ARGS;
        }
        std::shared_ptr<MultiNetDef> multi_net_def;
        const unsigned char *model_data;
        (void) model_weights_data;
        (void) model_weights_data_size;

        MaceStatus status = MaceStatus::MACE_SUCCESS;
        switch (model_name_map[model_name]) {
            case 0: {
                multi_net_def = mace::mobilenet_v1::CreateMultiNet();
                engine->reset(new mace::MaceEngine(config));
                model_data = mace::mobilenet_v1::LoadModelData();
                const int64_t model_size = mace::mobilenet_v1::GetModelSize();
                status = (*engine)->Init(
                        multi_net_def.get(), input_nodes, output_nodes,
                        model_data, model_size, model_data_unused, tutor, fake_warmup);
                break;
            }
            case 1: {
                multi_net_def = mace::mobilenet_v2::CreateMultiNet();
                engine->reset(new mace::MaceEngine(config));
                model_data = mace::mobilenet_v2::LoadModelData();
                const int64_t model_size = mace::mobilenet_v2::GetModelSize();
                status = (*engine)->Init(
                        multi_net_def.get(), input_nodes, output_nodes,
                        model_data, model_size, model_data_unused, tutor, fake_warmup);
                break;
            }
            case 2: {
                multi_net_def = mace::mobilenet_v1_quant::CreateMultiNet();
                engine->reset(new mace::MaceEngine(config));
                model_data = mace::mobilenet_v1_quant::LoadModelData();
                const int64_t model_size = mace::mobilenet_v1_quant::GetModelSize();
                status = (*engine)->Init(
                        multi_net_def.get(), input_nodes, output_nodes,
                        model_data, model_size, model_data_unused, tutor, fake_warmup);
                break;
            }
            case 3: {
                multi_net_def = mace::mobilenet_v2_quant::CreateMultiNet();
                engine->reset(new mace::MaceEngine(config));
                model_data = mace::mobilenet_v2_quant::LoadModelData();
                const int64_t model_size = mace::mobilenet_v2_quant::GetModelSize();
                status = (*engine)->Init(
                        multi_net_def.get(), input_nodes, output_nodes,
                        model_data, model_size, model_data_unused, tutor, fake_warmup);
                break;
            }
            default:
                status = MaceStatus::MACE_INVALID_ARGS;
        }

        return status;
    }

}  // namespace mace
#endif  // MACE_CODEGEN_ENGINE_MACE_ENGINE_FACTORY_H_
